package com.example.webstoreservice.service.impl;

import com.example.webstoreservice.feign.UserClient;
import com.example.webstoreservice.model.dto.UserDto;
import com.example.webstoreservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса {@link UserService} для работы с пользователями.
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserClient userService;

  /**
   * Получение аутентифицированного пользователя.
   *
   * @return Объект {@link UserDto}, представляющий аутентифицированного пользователя.
   * @throws EntityNotFoundException, если пользователь не аутентифицирован.
   */
  @Override
  public UserDto getAuthenticatedUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    if (username == null || username.isEmpty()) {
      throw new EntityNotFoundException("Пользователь не аутентифицирован");
    }
    return userService.getUserDtoByUsername(username);
  }
}