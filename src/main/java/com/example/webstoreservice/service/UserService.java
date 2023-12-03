package com.example.webstoreservice.service;

import com.example.webstoreservice.model.dto.UserDto;

public interface UserService {

  UserDto getAuthenticatedUser();
}
