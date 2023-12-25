package com.example.webstoreservice.controller;

import com.example.webstoreservice.model.dto.BookDto;
import com.example.webstoreservice.model.enums.SortBy;
import com.example.webstoreservice.model.enums.SortDirection;
import com.example.webstoreservice.service.BookService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления книгами. Предоставляет методы получения страниц с книгами,
 * информации о конкретной книге и обложки книги.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {

  private final BookService bookService;

  /**
   * Получение страницы с книгами с учетом параметров фильтрации и сортировки.
   *
   * @param title          Фильтрация по названию книги.
   * @param authorId       Фильтрация по идентификатору автора.
   * @param genreId        Фильтрация по идентификатору жанра.
   * @param minPrice       Фильтрация по минимальной цене книги.
   * @param maxPrice       Фильтрация по максимальной цене книги.
   * @param sortBy         Поле, по которому выполняется сортировка ({@link SortBy#TITLE},
   *                       {@link SortBy#PRICE}, {@link SortBy#PUBLICATION_YEAR}).
   * @param sortDirection  Направление сортировки ({@link SortDirection#ASC} или
   *                       {@link SortDirection#DESC}).
   * @param page           Номер страницы при постраничном выводе.
   * @param size           Количество объектов на странице при постраничном выводе.
   * @return Объект {@link ResponseEntity} со списком книг {@link Page<BookDto>}.
   */
  @GetMapping
  public ResponseEntity<Page<BookDto>> getBooks(
      @RequestParam(name = "title", required = false) String title,
      @RequestParam(name = "author", required = false) Long authorId,
      @RequestParam(name = "genre", required = false) Long genreId,
      @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
      @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
      @RequestParam(name = "sortBy", required = false, defaultValue = "TITLE") SortBy sortBy,
      @RequestParam(name = "sortDirection",
          required = false, defaultValue = "ASC") SortDirection sortDirection,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "1") int size) {
    PageRequest pageable = PageRequest.of(page, size);
    Page<BookDto> books = bookService.getBooks(title, authorId, genreId, minPrice, maxPrice,
        sortBy, sortDirection, pageable);
    return ResponseEntity.ok(books);
  }

  /**
   * Получение информации о конкретной книге по идентификатору.
   *
   * @param bookId Идентификатор книги.
   * @return Объект {@link ResponseEntity} с информацией о книге {@link BookDto}.
   */
  @GetMapping("/{bookId}")
  public ResponseEntity<BookDto> getBookById(@PathVariable Long bookId) {
    return ResponseEntity.ok(bookService.getBookDtoById(bookId));
  }

  /**
   * Получение обложки книги по идентификатору.
   *
   * @param bookId Идентификатор книги.
   * @return Объект {@link ResponseEntity} изображения обложки в виде массива байтов.
   */
  @GetMapping("/{bookId}/image")
  public ResponseEntity<byte[]> getBookCover(@PathVariable Long bookId) {
    return ResponseEntity.ok()
        .contentType(MediaType.IMAGE_JPEG)
        .body(bookService.getBookCover(bookId));
  }
}