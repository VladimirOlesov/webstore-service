package com.example.webstoreservice.controller;

import com.example.webstoreservice.model.dto.AuthorDto;
import com.example.webstoreservice.model.enums.SortDirection;
import com.example.webstoreservice.service.AuthorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления данными об авторах книг. Предоставляет методы получения списка авторов
 * с учетом фильтрации и сортировки.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/authors")
public class AuthorController {

  private final AuthorService authorService;

  /**
   * Получение списка авторов с учетом параметров фильтрации и сортировки.
   *
   * @param authorName    Фильтрация по имени автора.
   * @param sortDirection Направление сортировки ({@link SortDirection#ASC} или
   *                      {@link SortDirection#DESC}).
   * @return Объект {@link ResponseEntity} с списком авторов {@link AuthorDto}.
   */
  @GetMapping
  public ResponseEntity<List<AuthorDto>> gerAuthors(
      @RequestParam(name = "authorName", required = false) String authorName,
      @RequestParam(name = "sortDirection", required = false) SortDirection sortDirection) {
    return ResponseEntity.ok(authorService.getAuthors(authorName, sortDirection));
  }
}