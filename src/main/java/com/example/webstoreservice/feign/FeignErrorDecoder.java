package com.example.webstoreservice.feign;

import com.example.commoncode.model.dto.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FeignErrorDecoder implements ErrorDecoder {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @SneakyThrows
  @Override
  public Exception decode(String methodKey, Response response) {
    String message = HttpStatus.valueOf(response.status()).getReasonPhrase();

    if (response.body() != null) {
      ErrorResponseDto errorResponse = objectMapper.readValue(response.body().asInputStream(),
          ErrorResponseDto.class);
      message = errorResponse.message();
    }

    return new ResponseStatusException(HttpStatus.valueOf(response.status()), message);
  }
}
