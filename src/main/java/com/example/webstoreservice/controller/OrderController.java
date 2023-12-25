package com.example.webstoreservice.controller;

import com.example.webstoreservice.model.dto.OrderDto;
import com.example.webstoreservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления заказами книг. Предоставляет методы для работы с корзиной,
 * подтверждения и отмены заказов.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

  private final OrderService orderService;

  /**
   * Добавление книги в корзину пользователя.
   *
   * @param bookId Идентификатор книги.
   * @return Объект {@link ResponseEntity} с информацией о заказе {@link OrderDto}.
   */
  @PostMapping("/in-cart/{bookId}")
  public ResponseEntity<OrderDto> addToCart(@PathVariable Long bookId) {
    return ResponseEntity.ok(orderService.addToCart(bookId));
  }

  /**
   * Получение текущей корзины пользователя.
   *
   * @return Объект {@link ResponseEntity} с информацией о текущей корзине {@link OrderDto}.
   */
  @GetMapping("/cart")
  public ResponseEntity<OrderDto> getCart() {
    return ResponseEntity.ok(orderService.getCart());
  }

  /**
   * Удаление книги из корзины пользователя.
   *
   * @param bookId Идентификатор книги.
   * @return Объект {@link ResponseEntity} без тела с кодом успешного выполнения.
   */
  @DeleteMapping("/in-cart/{bookId}")
  public ResponseEntity<Void> removeFromCart(@PathVariable Long bookId) {
    orderService.removeFromCart(bookId);
    return ResponseEntity.ok().build();
  }

  /**
   * Очистка корзины пользователя.
   *
   * @return Объект {@link ResponseEntity} без тела с кодом успешного выполнения.
   */
  @DeleteMapping("/empty-cart")
  public ResponseEntity<Void> clearCart() {
    orderService.clearCart();
    return ResponseEntity.ok().build();
  }

  /**
   * Подтверждение заказа пользователя.
   *
   * @return Объект {@link ResponseEntity} с информацией о подтвержденном заказе {@link OrderDto}.
   */
  @PostMapping("/confirmation")
  public ResponseEntity<OrderDto> confirmOrder() {
    return ResponseEntity.ok(orderService.confirmOrder());
  }

  /**
   * Отмена заказа пользователя по его идентификатору.
   *
   * @param orderId Идентификатор заказа.
   * @return Объект {@link ResponseEntity} без тела с кодом успешного выполнения.
   */
  @DeleteMapping("/cancellation/{orderId}")
  public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
    orderService.cancelOrder(orderId);
    return ResponseEntity.ok().build();
  }
}