package com.example.webstoreservice.controller;

import com.example.webstoreservice.feign.AuthServiceClient;
import com.example.webstoreservice.model.dto.UserDtoLogin;
import com.example.webstoreservice.model.dto.UserDtoRegister;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthServiceClient authService;

  @PostMapping("/register")
  public ResponseEntity<UserDtoRegister> register(@Valid @RequestBody UserDtoRegister userDto) {
    return ResponseEntity.ok(authService.register(userDto));
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@Valid @RequestBody UserDtoLogin userDto) {
    return ResponseEntity.ok(authService.authenticate(userDto));
  }
}