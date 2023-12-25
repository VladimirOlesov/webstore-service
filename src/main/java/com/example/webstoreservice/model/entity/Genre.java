package com.example.webstoreservice.model.entity;

import com.example.commoncode.model.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * Сущность для хранения информации о жанрах электронных книг.
 * Наследует поле 'id' от базового класса BaseEntity.
 */
@Builder
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@AttributeOverride(name = "id", column = @Column(name = "genre_id"))
@Table(name = "genres")
public class Genre extends BaseEntity {

  /**
   * Название жанра.
   */
  @Column(name = "genre_name", nullable = false)
  private String genreName;
}