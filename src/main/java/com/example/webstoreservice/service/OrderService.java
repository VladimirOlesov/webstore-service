package com.example.webstoreservice.service;

import com.example.webstoreservice.model.dto.OrderDto;

public interface OrderService {

  OrderDto getCart();

  OrderDto addToCart(Long bookId);

  void removeFromCart(Long bookId);

  void clearCart();

  OrderDto confirmOrder();

  void cancelOrder(Long orderId);
}