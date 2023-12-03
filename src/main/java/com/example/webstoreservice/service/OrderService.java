package com.example.webstoreservice.service;

import com.example.webstoreservice.model.dto.OrderDto;
import com.example.webstoreservice.model.entity.Order;

public interface OrderService {

  OrderDto getCart();

  Order getCartInternal();

  OrderDto addToCart(Long bookId);

  void removeFromCart(Long bookId);

  void clearCart();

  OrderDto confirmOrder();

  void cancelOrder(Long orderId);
}