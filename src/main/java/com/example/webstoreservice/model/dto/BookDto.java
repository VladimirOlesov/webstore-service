package com.example.webstoreservice.model.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

@Builder
@FieldNameConstants
public record BookDto(
    Long bookId,
    String title,
    @NotNull AuthorDto author,
    @NotNull GenreDto genre,
    Integer publicationYear,
    BigDecimal price,
    String ISBN,
    Integer pageCount,
    Integer ageRating,
    String coverPath,
    boolean deleted) {

}