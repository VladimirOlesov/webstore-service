package com.example.webstoreservice.model.dto;

import lombok.Builder;

@Builder
public record ErrorResponseDto(String message) {

}