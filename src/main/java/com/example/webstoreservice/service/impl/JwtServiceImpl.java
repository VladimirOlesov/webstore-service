package com.example.webstoreservice.service.impl;

import com.example.webstoreservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса {@link JwtService} для работы с JSON Web Token (JWT).
 */
@Service
public class JwtServiceImpl implements JwtService {

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.claim.uuid}")
  private String claimUuid;

  /**
   * Извлечение имени пользователя из JWT.
   *
   * @param token JWT-токен.
   * @return Имя пользователя.
   */
  @Override
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Извлечение UUID из JWT.
   *
   * @param token JWT-токен.
   * @return Строковое представление UUID.
   */
  @Override
  public String extractUuid(String token) {
    return extractClaim(token, claims -> claims.get(claimUuid, String.class));
  }

  /**
   * Проверка валидности JWT-токена для конкретного пользователя.
   *
   * @param token       JWT-токен.
   * @param userDetails Информация о пользователе.
   * @return true, если токен валиден, в противном случае - false.
   */
  @Override
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSecretKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSecretKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}