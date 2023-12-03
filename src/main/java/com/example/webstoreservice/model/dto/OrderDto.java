package com.example.webstoreservice.model.dto;

import com.example.webstoreservice.model.entity.Book;
import com.example.webstoreservice.model.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderDto(
    Long orderId,
    UUID userUuid,
    LocalDateTime orderDate,
    OrderStatus status,
    Set<Book> books) {

}