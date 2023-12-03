package com.example.webstoreservice.model.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class ValidationErrorDto {

  private final Map<String, String> fieldErrors = new HashMap<>();

  public void addFieldError(String field, String message) {
    fieldErrors.put(field, message);
  }
}