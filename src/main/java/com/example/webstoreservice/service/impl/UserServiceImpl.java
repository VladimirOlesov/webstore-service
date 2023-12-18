package com.example.webstoreservice.service.impl;

import com.example.webstoreservice.feign.UserClient;
import com.example.webstoreservice.model.dto.UserDto;
import com.example.webstoreservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserClient userService;

  @Override
  public UserDto getAuthenticatedUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    if (username == null || username.isEmpty()) {
      throw new EntityNotFoundException("Пользователь не аутентифицирован");
    }
    return userService.getUserDtoByUsername(username);
  }
}