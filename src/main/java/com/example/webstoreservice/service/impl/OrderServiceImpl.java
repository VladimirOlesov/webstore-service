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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;

  private final BookService bookService;

  private final OrderMapper orderMapper;

  private final UserService userService;

  @Override
  public OrderDto getCart() {
    UserDto user = userService.getAuthenticatedUser();
    return orderRepository.findByUserUuidAndStatusWithBooks(user.userUuid(), OrderStatus.IN_CART)
        .map(orderMapper::toOrderDto)
        .orElse(OrderDto.builder().status(OrderStatus.IN_CART).books(Set.of()).build());
  }

  @Override
  public Order getCartInternal() {
    UserDto user = userService.getAuthenticatedUser();
    return orderRepository.findByUserUuidAndStatus(user.userUuid(), OrderStatus.IN_CART)
        .orElseThrow(() -> new EntityNotFoundException("Корзина не найдена"));
  }

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

  @Override
  @Transactional
  public void clearCart() {
    orderRepository.delete(getCartInternal());
  }

  @Override
  @Transactional
  public OrderDto confirmOrder() {
    Order cartOrder = getCartInternal();

    cartOrder.setStatus(OrderStatus.COMPLETED);
    cartOrder.setOrderDate(LocalDateTime.now());

    orderRepository.save(cartOrder);

    return orderMapper.toOrderDto(cartOrder);
  }

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
  }
}