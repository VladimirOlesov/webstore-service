package com.example.webstoreservice.feign;

import com.example.webstoreservice.model.dto.UserDto;
import com.example.webstoreservice.model.dto.UserDtoLogin;
import com.example.webstoreservice.model.dto.UserDtoRegister;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${feign.name}", url = "${feign.url}")
public interface AuthServiceClient {

  @GetMapping("/users/uuid/{uuid}")
  UserDto getUserDtoByUuid(@Valid @PathVariable UUID uuid);

  @GetMapping("/users/username/{username}")
  UserDto getUserDtoByUsername(@PathVariable String username);

  @PostMapping("/auth/register")
  UserDtoRegister register(@Valid @RequestBody UserDtoRegister userDto);

  @PostMapping("/auth/login")
  String authenticate(@Valid @RequestBody UserDtoLogin userDto);
}
