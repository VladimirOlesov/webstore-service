package com.example.webstoreservice.model.dto;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;

@Builder
@FieldNameConstants
public record AuthorDto(
    Long authorId,
    String authorName) {

}