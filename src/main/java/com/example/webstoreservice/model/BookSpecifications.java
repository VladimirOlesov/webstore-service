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

public class BookSpecifications {

  public static Specification<Book> titleContains(String title) {
    return (root, query, criteriaBuilder) -> {
      if (StringUtils.isEmpty(title)) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(root.get(Book_.title),
          MessageFormat.format("%{0}%", title));
    };
  }

  public static Specification<Book> authorIs(Long authorId) {
    return (root, query, criteriaBuilder) -> {
      if (authorId == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get(Book_.author).get(Author_.id), authorId);
    };
  }

  public static Specification<Book> genreIs(Long genreId) {
    return (root, query, criteriaBuilder) -> {
      if (genreId == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get(Book_.genre).get(Genre_.id), genreId);
    };
  }

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

  public static Specification<Book> notDeleted() {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get(Book_.deleted)));
  }
}