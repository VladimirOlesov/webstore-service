package com.example.webstoreservice.controller;

import com.example.webstoreservice.model.dto.GenreDto;
import com.example.webstoreservice.model.enums.SortDirection;
import com.example.webstoreservice.service.GenreService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления жанрами книг. Предоставляет методы получения списка жанров с учетом
 * фильтрации и сортировки.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreController {

  private final GenreService genreService;

  /**
   * Получение списка жанров с учетом параметров фильтрации и сортировки.
   *
   * @param genreName     Фильтрация по названию жанра.
   * @param sortDirection Направление сортировки ({@link SortDirection#ASC} или
   *                      {@link SortDirection#DESC}).
   * @return Объект {@link ResponseEntity} со списком жанров {@link GenreDto}.
   */
  @GetMapping
  public ResponseEntity<List<GenreDto>> getGenres(
      @RequestParam(name = "genreName", required = false) String genreName,
      @RequestParam(name = "sortDirection", required = false) SortDirection sortDirection) {
    return ResponseEntity.ok(genreService.getGenres(genreName, sortDirection));
  }
}