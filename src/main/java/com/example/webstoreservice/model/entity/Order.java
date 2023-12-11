package com.example.webstoreservice.model.entity;

import com.example.commoncode.model.entity.BaseEntity;
import com.example.webstoreservice.model.enums.OrderStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Builder
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@AttributeOverride(name = "id", column = @Column(name = "order_id"))
@Table(name = "orders")
public class Order extends BaseEntity {

  @Column(name = "user_uuid", nullable = false)
  private UUID userUuid;

  @Column(name = "order_date", columnDefinition = "timestamp")
  private LocalDateTime orderDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private OrderStatus status;

  @Builder.Default
  @ManyToMany
  @JoinTable(
      name = "orders_books",
      joinColumns = @JoinColumn(name = "order_id"),
      inverseJoinColumns = @JoinColumn(name = "book_id")
  )
  private Set<Book> books = new HashSet<>();
}