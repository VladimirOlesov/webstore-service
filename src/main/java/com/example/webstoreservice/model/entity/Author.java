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
 * Сущность для хранения информации об авторах электронных книг.
 * Наследует поле 'id' от базового класса BaseEntity.
 */
@Builder
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@AttributeOverride(name = "id", column = @Column(name = "author_id"))
@Table(name = "authors")
public class Author extends BaseEntity {

  /**
   * Имя автора.
   */
  @Column(name = "author_name", nullable = false)
  private String authorName;
}