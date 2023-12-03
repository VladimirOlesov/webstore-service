package com.example.webstoreservice.controller;

import com.example.webstoreservice.model.dto.BookDto;
import com.example.webstoreservice.model.dto.FavoriteIdDto;
import com.example.webstoreservice.service.FavoriteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/favorites")
public class FavoriteController {

  private final FavoriteService favoriteService;

  @GetMapping
  public ResponseEntity<List<BookDto>> getFavoriteBooks() {
    return ResponseEntity.ok(favoriteService.getFavoriteBooks());
  }

  @PostMapping("/{bookId}")
  public ResponseEntity<FavoriteIdDto> addToFavorites(@PathVariable Long bookId) {
    return ResponseEntity.ok(favoriteService.addToFavorites(bookId));
  }

  @DeleteMapping("/{bookId}")
  public ResponseEntity<Void> removeFromFavorites(@PathVariable Long bookId) {
    favoriteService.removeFromFavorites(bookId);
    return ResponseEntity.ok().build();
  }
}