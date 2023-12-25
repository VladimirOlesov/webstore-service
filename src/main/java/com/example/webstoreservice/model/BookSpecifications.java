package com.example.webstoreservice.model;

import com.example.webstoreservice.model.entity.Author_;
import com.example.webstoreservice.model.entity.Book;
import com.example.webstoreservice.model.entity.Book_;
import com.example.webstoreservice.model.entity.Genre_;
import com.example.webstoreservice.model.enums.SortBy;
import com.example.webstoreservice.model.enums.SortDirection;
import java.math.BigDecimal;
import java.text.MessageFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

/**
 * Класс с методами для создания спецификаций {@link Specification} для сущности {@link Book}.
 */
public class BookSpecifications {

  /**
   * Создание спецификации для фильтрации по названию книги.
   *
   * @param title Название книги для фильтрации.
   * @return Спецификация для фильтрации по названию книги.
   */
  public static Specification<Book> titleContains(String title) {
    return (root, query, criteriaBuilder) -> {
      if (StringUtils.isEmpty(title)) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(root.get(Book_.title),
          MessageFormat.format("%{0}%", title));
    };
  }

  /**
   * Создание спецификации для фильтрации по идентификатору автора книги.
   *
   * @param authorId Идентификатор автора для фильтрации.
   * @return Спецификация для фильтрации по идентификатору автора книги.
   */
  public static Specification<Book> authorIs(Long authorId) {
    return (root, query, criteriaBuilder) -> {
      if (authorId == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get(Book_.author).get(Author_.id), authorId);
    };
  }

  /**
   * Создание спецификации для фильтрации по идентификатору жанра книги.
   *
   * @param genreId Идентификатор жанра для фильтрации.
   * @return Спецификация для фильтрации по идентификатору жанра книги.
   */
  public static Specification<Book> genreIs(Long genreId) {
    return (root, query, criteriaBuilder) -> {
      if (genreId == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get(Book_.genre).get(Genre_.id), genreId);
    };
  }

  /**
   * Создание спецификации для фильтрации по цене книги в заданном диапазоне.
   *
   * @param minPrice Минимальная цена книги для фильтрации.
   * @param maxPrice Максимальная цена книги для фильтрации.
   * @return Спецификация для фильтрации по цене книги в заданном диапазоне.
   */
  public static Specification<Book> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
    return (root, query, criteriaBuilder) -> {
      if (minPrice == null && maxPrice == null) {
        return criteriaBuilder.conjunction();
      }
      if (minPrice != null && maxPrice != null) {
        return criteriaBuilder.between(root.get(Book_.price), minPrice, maxPrice);
      } else if (minPrice != null) {
        return criteriaBuilder.greaterThanOrEqualTo(root.get(Book_.price), minPrice);
      } else {
        return criteriaBuilder.lessThanOrEqualTo(root.get(Book_.price), maxPrice);
      }
    };
  }

  /**
   * Создание спецификации для сортировки результатов запроса.
   *
   * @param sortBy        Поле, по которому выполняется сортировка ({@link SortBy#TITLE},
   *                      {@link SortBy#PRICE}, {@link SortBy#PUBLICATION_YEAR}).
   * @param sortDirection Направление сортировки ){@link SortDirection#ASC} или
   *                      {@link SortDirection#DESC}).
   * @return Спецификация для сортировки результатов запроса.
   * @throws IllegalArgumentException, если параметры сортировки имеют недопустимые значения.
   */
  public static Specification<Book> orderBy(SortBy sortBy, SortDirection sortDirection) {
    if (sortBy == null || sortDirection == null) {
      throw new IllegalArgumentException("Параметры сортировки имеют недопустимые значения");
    }

    return (root, query, criteriaBuilder) -> {

      query.orderBy((sortDirection == SortDirection.DESC) ?
          criteriaBuilder.desc(root.get(sortBy.getField())) :
          criteriaBuilder.asc(root.get(sortBy.getField())));

      return query.getRestriction();
    };
  }

  /**
   * Создание спецификации для фильтрации неудаленных книг.
   *
   * @return Спецификация для фильтрации неудаленных книг.
   */
  public static Specification<Book> notDeleted() {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get(Book_.deleted)));
  }
}