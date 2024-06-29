package com.example.webstoreservice.feign;

import com.example.webstoreservice.config.JwtAuthenticationFilter;
import com.example.webstoreservice.model.dto.UserDto;
import feign.RequestInterceptor;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign-клиент для взаимодействия с сервисом регистрации и аутентификации.
 */
@FeignClient(name = "${feign.webstore-auth-service.user-client.name}",
    url = "${feign.webstore-auth-service.user-client.url}",
    configuration = {UserClient.UserFeignClientConfig.class, FeignErrorConfig.class})
public interface UserClient {

  /**
   * Получение данных пользователя по уникальному идентификатору в сервисе регистрации и
   * аутентификации.
   *
   * @param uuid UUID пользователя.
   * @return Объект {@link UserDto} с данными пользователя.
   */
  @GetMapping("/users/uuid/{uuid}")
  UserDto getUserDtoByUuid(@Valid @PathVariable UUID uuid);

  /**
   * Получение данных пользователя по имени пользователя в сервисе регистрации и аутентификации.
   *
   * @param username Имя пользователя.
   * @return Объект {@link UserDto} с данными пользователя.
   */
  @GetMapping("/users/username/{username}")
  UserDto getUserDtoByUsername(@PathVariable String username);

  /**
   * Конфигурация Feign-клиента для добавления заголовка с JWT-токеном в запрос.
   */
  @Configuration
  class UserFeignClientConfig {

    /**
     * Добавление JWT-токена в заголовок запроса перед выполнением запроса к сервису регистрации и
     * аутентификации.
     *
     * @return Объект {@link RequestInterceptor}.
     */
    @Bean
    public RequestInterceptor feignRequestInterceptor() {
      return requestTemplate -> {
        String token = JwtAuthenticationFilter.getCurrentToken();
        if (token != null) {
          requestTemplate.header("Authorization", "Bearer " + token);
        }
      };
    }
  }
}