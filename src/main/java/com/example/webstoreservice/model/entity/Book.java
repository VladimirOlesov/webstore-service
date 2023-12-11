package com.example.webstoreservice.model.entity;

import com.example.commoncode.model.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
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
@AttributeOverride(name = "id", column = @Column(name = "book_id"))
@Table(name = "books")
public class Book extends BaseEntity {

  @Column(name = "title", nullable = false)
  private String title;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false)
  private Author author;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "genre_id", nullable = false)
  private Genre genre;

  @Column(name = "publication_year")
  private Integer publicationYear;

  @Column(name = "price", precision = 8, scale = 2)
  private BigDecimal price;

  @Column(name = "ISBN", nullable = false, unique = true)
  private String ISBN;

  @Column(name = "page_count")
  private Integer pageCount;

  @Column(name = "age_rating")
  private Integer ageRating;

  @Column(name = "cover_path")
  private String coverPath;

  @Column(name = "deleted", nullable = false)
  private Boolean deleted;
}