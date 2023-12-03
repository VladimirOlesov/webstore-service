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

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

  private final OrderService orderService;

  @PostMapping("/in-cart/{bookId}")
  public ResponseEntity<OrderDto> addToCart(@PathVariable Long bookId) {
    return ResponseEntity.ok(orderService.addToCart(bookId));
  }

  @GetMapping("/cart")
  public ResponseEntity<OrderDto> getCart() {
    return ResponseEntity.ok(orderService.getCart());
  }

  @DeleteMapping("/in-cart/{bookId}")
  public ResponseEntity<Void> removeFromCart(@PathVariable Long bookId) {
    orderService.removeFromCart(bookId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/empty-cart")
  public ResponseEntity<Void> clearCart() {
    orderService.clearCart();
    return ResponseEntity.ok().build();
  }

  @PostMapping("/confirmation")
  public ResponseEntity<OrderDto> confirmOrder() {
    return ResponseEntity.ok(orderService.confirmOrder());
  }

  @DeleteMapping("/cancellation/{orderId}")
  public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
    orderService.cancelOrder(orderId);
    return ResponseEntity.ok().build();
  }
}