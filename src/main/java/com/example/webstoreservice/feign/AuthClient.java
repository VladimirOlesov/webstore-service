package com.example.webstoreservice.feign;

import com.example.webstoreservice.model.dto.UserDtoLogin;
import com.example.webstoreservice.model.dto.UserDtoRegister;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign-клиент для взаимодействия с сервисом регистрации и аутентификации.
 */
@FeignClient(name = "${feign.webstore-auth-service.auth-client.name}",
    url = "${feign.webstore-auth-service.auth-client.url}")
public interface AuthClient {

  /**
   * Регистрация нового пользователя в сервисе регистрации и аутентификации.
   *
   * @param userDto Данные пользователя для регистрации.
   * @return Объект {@link UserDtoRegister} зарегистрированного пользователя.
   */
  @PostMapping("/auth/register")
  UserDtoRegister register(@Valid @RequestBody UserDtoRegister userDto);

  /**
   * Аутентификация пользователя в сервисе регистрации и аутентификации.
   *
   * @param userDto Данные пользователя для аутентификации.
   * @return Строка, представляющая сгенерированный токен, для аутентифицированного пользователя.
   */
  @PostMapping("/auth/login")
  String authenticate(@Valid @RequestBody UserDtoLogin userDto);
}
