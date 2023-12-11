package com.example.webstoreservice.feign;

import com.example.webstoreservice.config.JwtAuthenticationFilter;
import com.example.webstoreservice.model.dto.UserDto;
import com.example.webstoreservice.model.dto.UserDtoLogin;
import com.example.webstoreservice.model.dto.UserDtoRegister;
import feign.RequestInterceptor;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${feign.webstore-auth-service.name}", url = "${feign.webstore-auth-service.url}",
    configuration = AuthServiceClient.FeignClientConfig.class)
public interface AuthServiceClient {

  @GetMapping("/users/uuid/{uuid}")
  UserDto getUserDtoByUuid(@Valid @PathVariable UUID uuid);

  @GetMapping("/users/username/{username}")
  UserDto getUserDtoByUsername(@PathVariable String username);

  @PostMapping("/auth/register")
  UserDtoRegister register(@Valid @RequestBody UserDtoRegister userDto);

  @PostMapping("/auth/login")
  String authenticate(@Valid @RequestBody UserDtoLogin userDto);

  @Configuration
  class FeignClientConfig {

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
      return requestTemplate -> {
        if (requestTemplate.method().equals("POST") &&
            (requestTemplate.url().contains("/auth/register") || requestTemplate.url()
                .contains("/auth/login"))) {
          return;
        }
        String token = JwtAuthenticationFilter.getCurrentToken();
        if (token != null) {
          requestTemplate.header("Authorization", "Bearer " + token);
        }
      };
    }
  }
}