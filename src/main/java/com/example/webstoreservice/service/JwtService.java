package com.example.webstoreservice.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

  String extractUsername(String token);

  String extractUuid(String token);

  boolean isTokenValid(String jwt, UserDetails userDetails);
}