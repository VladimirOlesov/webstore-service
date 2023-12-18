package com.example.webstoreservice.config;

import com.example.webstoreservice.feign.UserClient;
import com.example.webstoreservice.model.dto.UserDto;
import com.example.webstoreservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  private final UserClient userService;

  private final static ThreadLocal<String> tokenThreadLocal = new ThreadLocal<>();

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String uuid;
    if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    jwt = authHeader.substring(7);
    uuid = jwtService.extractUuid(jwt);
    if (uuid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      tokenThreadLocal.set(jwt);
      UserDto userDto = userService.getUserDtoByUuid(UUID.fromString(uuid));
      UserDetails userDetails = User.builder()
          .username(userDto.username())
          .password(userDto.password())
          .authorities(List.of(new SimpleGrantedAuthority(userDto.role().name())))
          .build();
      if (jwtService.isTokenValid(jwt, userDetails)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
    tokenThreadLocal.remove();
  }

  public static String getCurrentToken() {
    return tokenThreadLocal.get();
  }
}