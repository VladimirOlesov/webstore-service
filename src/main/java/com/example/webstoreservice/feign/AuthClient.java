package com.example.webstoreservice.feign;

import com.example.webstoreservice.model.dto.UserDtoLogin;
import com.example.webstoreservice.model.dto.UserDtoRegister;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${feign.webstore-auth-service.auth-client.name}",
    url = "${feign.webstore-auth-service.auth-client.url}")
public interface AuthClient {

  @PostMapping("/auth/register")
  UserDtoRegister register(@Valid @RequestBody UserDtoRegister userDto);

  @PostMapping("/auth/login")
  String authenticate(@Valid @RequestBody UserDtoLogin userDto);
}
