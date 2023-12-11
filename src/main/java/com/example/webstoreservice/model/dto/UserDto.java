package com.example.webstoreservice.model.dto;

import com.example.commoncode.model.enums.Role;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserDto(
    Long userId,
    UUID userUuid,
    String username,
    String password,
    String email,
    String phoneNumber,
    LocalDateTime registrationDate,
    String firstName,
    String lastName,
    Role role
) {

}