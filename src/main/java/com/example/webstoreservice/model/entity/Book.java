package com.example.webstoreservice.model.entity;

import com.example.commoncode.model.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * Сущность для хранения информации об электронных книгах.
 * Наследует поле 'id' от базового класса BaseEntity.
 */
@Builder(toBuilder = true)
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@AttributeOverride(name = "id", column = @Column(name = "book_id"))
@Table(name = "books")
public class Book extends BaseEntity {

  /**
   * Версия для оптимистичной блокировки.
   */
  @Version
  @Column(name = "version", nullable = false)
  private Long version;

  /**
   * Название книги.
   */
  @Column(name = "title", nullable = false)
  private String title;

  /**
   * Автор книги.
   */
  @NotNull
  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false)
  private Author author;

  /**
   * Жанр книги
   */
  @NotNull
  @ManyToOne
  @JoinColumn(name = "genre_id", nullable = false)
  private Genre genre;

  /**
   * Год публикации книги.
   */
  @Column(name = "publication_year")
  private Integer publicationYear;

  /**
   * Цена книги.
   */
  @Column(name = "price", precision = 8, scale = 2)
  private BigDecimal price;

  /**
   * ISBN книги.
   */
  @Column(name = "ISBN", nullable = false, unique = true)
  private String ISBN;

  /**
   * Количество страниц в книге.
   */
  @Column(name = "page_count")
  private Integer pageCount;

  /**
   * Возрастной рейтинг книги.
   */
  @Column(name = "age_rating")
  private Integer ageRating;

  /**
   * Путь к обложке книги.
   */
  @Column(name = "cover_path")
  private String coverPath;

  /**
   * Флаг, сигнализирующий об удалении книги.
   */
  @Column(name = "deleted", nullable = false)
  private Boolean deleted;
}