package com.example.webstoreservice.model.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record FavoriteIdDto(
    UUID userUuid,
    Long bookId) {

}