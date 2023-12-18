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

@FeignClient(name = "${feign.webstore-auth-service.user-client.name}",
    url = "${feign.webstore-auth-service.user-client.url}",
    configuration = UserClient.UserFeignClientConfig.class)
public interface UserClient {

  @GetMapping("/users/uuid/{uuid}")
  UserDto getUserDtoByUuid(@Valid @PathVariable UUID uuid);

  @GetMapping("/users/username/{username}")
  UserDto getUserDtoByUsername(@PathVariable String username);

  @Configuration
  class UserFeignClientConfig {

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