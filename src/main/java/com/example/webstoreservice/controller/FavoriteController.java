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

/**
 * Контроллер для управления списком избранных книг. Предоставляет методы получения списка избранных
 * книг, добавления книги в избранное и удаления книги из избранного.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/favorites")
public class FavoriteController {

  private final FavoriteService favoriteService;

  /**
   * Получение списка избранных книг для текущего пользователя.
   *
   * @return Объект {@link ResponseEntity} со списком книг {@link BookDto}.
   */
  @GetMapping
  public ResponseEntity<List<BookDto>> getFavoriteBooks() {
    return ResponseEntity.ok(favoriteService.getFavoriteBooks());
  }

  /**
   * Добавление книги в список избранных для текущего пользователя.
   *
   * @param bookId Идентификатор книги для добавления в избранное.
   * @return Объект {@link ResponseEntity} с идентификатором добавленной книги в избранное
   * {@link FavoriteIdDto}.
   */
  @PostMapping("/{bookId}")
  public ResponseEntity<FavoriteIdDto> addToFavorites(@PathVariable Long bookId) {
    return ResponseEntity.ok(favoriteService.addToFavorites(bookId));
  }

  /**
   * Удаление книги из списка избранных для текущего пользователя.
   *
   * @param bookId Идентификатор книги для удаления из избранного.
   * @return Объект {@link ResponseEntity} без тела с кодом успешного выполнения.
   */
  @DeleteMapping("/{bookId}")
  public ResponseEntity<Void> removeFromFavorites(@PathVariable Long bookId) {
    favoriteService.removeFromFavorites(bookId);
    return ResponseEntity.ok().build();
  }
}