package com.example.webstoreservice.exception;

public class DuplicateException extends RuntimeException {

  public DuplicateException(String message) {
    super(message);
  }
}