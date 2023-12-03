package com.example.webstoreservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class FavoriteId implements Serializable {

  @Column(name = "user_uuid", nullable = false)
  private UUID userUuid;

  @Column(name = "book_id", nullable = false)
  private Long bookId;
}