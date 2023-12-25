package com.example.webstoreservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сущность для хранения информации об избранных книгах пользователей.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "favorites")
public class Favorite {

  /**
   * Идентификатор составного ключа
   */
  @EmbeddedId
  private FavoriteId id;

  /**
   * Уникальный идентификатор пользователя
   */
  @Column(name = "user_uuid", insertable = false, updatable = false)
  private UUID userUuid;

  /**
   * Книга, относящаяся к избранному пользователей.
   */
  @ManyToOne
  @JoinColumn(name = "book_id", insertable = false, updatable = false)
  private Book book;
}
