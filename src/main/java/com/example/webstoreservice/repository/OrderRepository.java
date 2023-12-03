package com.example.webstoreservice.repository;

import com.example.webstoreservice.model.entity.Order;
import com.example.webstoreservice.model.enums.OrderStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

  Optional<Order> findByUserUuidAndStatus(UUID userUuid, OrderStatus orderStatus);

  Optional<Order> findByIdAndUserUuidAndStatus(Long orderId, UUID userUuid, OrderStatus orderStatus);

  @Query("""
      FROM Order o
      LEFT JOIN FETCH o.books
      WHERE o.userUuid = :userUuid AND o.status = :status
      """)
  Optional<Order> findByUserUuidAndStatusWithBooks(@Param("userUuid") UUID userUuid,
      @Param("status") OrderStatus status);
}