package com.example.webstoreservice.controller;

import com.example.webstoreservice.feign.AuthClient;
import com.example.webstoreservice.model.dto.UserDtoLogin;
import com.example.webstoreservice.model.dto.UserDtoRegister;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления аутентификацией и регистрацией пользователей. Предоставляет методы
 * регистрации нового пользователя и входа в систему.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthClient authClient;

  /**
   * Регистрация нового пользователя на основе предоставленных данных.
   *
   * @param userDto Данные нового пользователя {@link UserDtoRegister}.
   * @return Объект {@link ResponseEntity} с информацией о зарегистрированном пользователе.
   */
  @PostMapping("/register")
  public ResponseEntity<UserDtoRegister> register(@Valid @RequestBody UserDtoRegister userDto) {
    return ResponseEntity.ok(authClient.register(userDto));
  }

  /**
   * Вход в систему пользователя на основе предоставленных учетных данных.
   *
   * @param userDto Данные для аутентификации пользователя {@link UserDtoLogin}.
   * @return Объект {@link ResponseEntity} с токеном аутентификации.
   */
  @PostMapping("/login")
  public ResponseEntity<String> login(@Valid @RequestBody UserDtoLogin userDto) {
    return ResponseEntity.ok(authClient.authenticate(userDto));
  }
}