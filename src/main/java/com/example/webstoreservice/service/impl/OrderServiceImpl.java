package com.example.webstoreservice.service.impl;

import com.example.commoncode.exception.DuplicateException;
import com.example.webstoreservice.model.dto.OrderDto;
import com.example.webstoreservice.model.dto.UserDto;
import com.example.webstoreservice.model.entity.Book;
import com.example.webstoreservice.model.entity.Order;
import com.example.webstoreservice.model.enums.OrderStatus;
import com.example.webstoreservice.model.mapper.OrderMapper;
import com.example.webstoreservice.repository.OrderRepository;
import com.example.webstoreservice.service.BookService;
import com.example.webstoreservice.service.OrderService;
import com.example.webstoreservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса {@link OrderService} для работы с заказами.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;

  private final BookService bookService;

  private final OrderMapper orderMapper;

  private final UserService userService;

  private final KafkaTemplate<String, OrderDto> kafkaTemplate;

  /**
   * Получение корзины текущего пользователя.
   *
   * @return Объект {@link OrderDto}, представляющий корзину пользователя.
   */
  @Override
  public OrderDto getCart() {
    UserDto user = userService.getAuthenticatedUser();
    return orderRepository.findByUserUuidAndStatusWithBooks(user.userUuid(), OrderStatus.IN_CART)
        .map(orderMapper::toOrderDto)
        .orElse(OrderDto.builder().status(OrderStatus.IN_CART).books(Set.of()).build());
  }

  /**
   * Внутренний метод для получения корзины текущего пользователя без маппинга в dto.
   * В случае отсутствия корзины выбрасывает исключение {@link EntityNotFoundException}.
   *
   * @return Объект {@link Order}, представляющий корзину пользователя.
   * @throws EntityNotFoundException, если корзина не найдена.
   */
  public Order getCartInternal() {
    UserDto user = userService.getAuthenticatedUser();
    return orderRepository.findByUserUuidAndStatus(user.userUuid(), OrderStatus.IN_CART)
        .orElseThrow(() -> new EntityNotFoundException("Корзина не найдена"));
  }

  /**
   * Добавление книги в корзину пользователя.
   *
   * @param bookId Идентификатор книги.
   * @return Объект {@link OrderDto}, представляющий обновленную корзину пользователя.
   * @throws DuplicateException, если книга уже добавлена в корзину.
   */
  @Override
  @Transactional
  public OrderDto addToCart(Long bookId) {
    UserDto user = userService.getAuthenticatedUser();

    Book book = bookService.getBookById(bookId);

    Order cartOrder = orderRepository.findByUserUuidAndStatus(user.userUuid(), OrderStatus.IN_CART)
        .orElseGet(() -> orderRepository.save(Order.builder()
            .userUuid(user.userUuid())
            .status(OrderStatus.IN_CART)
            .build()));

    if (cartOrder.getBooks().contains(book)) {
      throw new DuplicateException("Книга уже добавлена в корзину");
    }

    cartOrder.getBooks().add(book);
    orderRepository.save(cartOrder);

    return orderMapper.toOrderDto(cartOrder);
  }

  /**
   * Удаление книги из корзины пользователя.
   *
   * @param bookId Идентификатор книги.
   * @throws EntityNotFoundException, если книга не найдена в корзине.
   */
  @Override
  @Transactional
  public void removeFromCart(Long bookId) {
    Order cartOrder = getCartInternal();
    Book bookToRemove = bookService.getBookById(bookId);

    if (!cartOrder.getBooks().remove(bookToRemove)) {
      throw new EntityNotFoundException("Книга не найдена в корзине");
    }

    if (cartOrder.getBooks().isEmpty()) {
      orderRepository.delete(cartOrder);
    } else {
      orderRepository.save(cartOrder);
    }
  }

  /**
   * Очистка корзины пользователя.
   */
  @Override
  @Transactional
  public void clearCart() {
    orderRepository.delete(getCartInternal());
  }

  /**
   * Подтверждение оформления заказа.
   *
   * @return Объект {@link OrderDto}, представляющий подтвержденный заказ.
   */
  @Override
  @Transactional
  public OrderDto confirmOrder() {
    Order cartOrder = getCartInternal();

    cartOrder.setStatus(OrderStatus.COMPLETED);
    cartOrder.setOrderDate(LocalDateTime.now());

    orderRepository.save(cartOrder);
    log.info("Заказ с id = {} успешно подтвержден", cartOrder.getId());

    OrderDto orderDto = orderMapper.toOrderDto(cartOrder);

    kafkaTemplate.send("order-topic", orderDto);

    return orderDto;
  }

  /**
   * Отмена оформленного заказа.
   *
   * @param orderId Идентификатор заказа.
   * @throws EntityNotFoundException, если заказ не найден.
   * @throws IllegalArgumentException, если срок отмены заказа истек.
   */
  @Override
  @Transactional
  public void cancelOrder(Long orderId) {
    UserDto user = userService.getAuthenticatedUser();

    Order confirmedOrder = orderRepository.findByIdAndUserUuidAndStatus(orderId, user.userUuid(),
            OrderStatus.COMPLETED)
        .orElseThrow(() -> new EntityNotFoundException("Завершенный заказ не найден"));

    if (ChronoUnit.DAYS.between(confirmedOrder.getOrderDate(), LocalDateTime.now()) > 1) {
      throw new IllegalArgumentException("Срок отмены заказа истек");
    }

    confirmedOrder.setStatus(OrderStatus.CANCELLED);

    orderRepository.save(confirmedOrder);
    log.info("Заказ с id = {} успешно отменен", confirmedOrder.getId());
  }
}