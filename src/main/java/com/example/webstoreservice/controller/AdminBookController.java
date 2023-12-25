package com.example.webstoreservice.controller;

import com.example.webstoreservice.model.dto.BookDto;
import com.example.webstoreservice.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Контроллер для управления книгами с правами администратора. Предоставляет методы создания,
 * обновления, удаления и экспорта книг.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/books")
public class AdminBookController {

  private final BookService bookService;

  /**
   * Создание новой книги на основе предоставленных данных.
   *
   * @param bookDto Данные для создания книги.
   * @return Объект {@link ResponseEntity} с информацией о созданной книге {@link BookDto}.
   */
  @PostMapping
  public ResponseEntity<BookDto> createBook(@RequestBody BookDto bookDto) {
    return ResponseEntity.ok(bookService.saveBook(bookDto));
  }

  /**
   * Обновление существующой книги на основе предоставленных данных.
   *
   * @param bookDto Данные для обновления книги.
   * @return Объект {@link ResponseEntity} с обновленной информацией о книге {@link BookDto}.
   */
  @PutMapping
  public ResponseEntity<BookDto> updateBook(@RequestBody BookDto bookDto) {
    return ResponseEntity.ok(bookService.updateBook(bookDto));
  }

  /**
   * Загрузка изображения для указанной книги.
   *
   * @param bookId Идентификатор книги.
   * @param file   Загружаемый файл изображения.
   * @return Объект {@link ResponseEntity} с информацией о пути сохраненного изображения.
   */
  @PostMapping("/{bookId}/image")
  public ResponseEntity<String> uploadImage(@PathVariable Long bookId,
      @RequestParam("file") MultipartFile file) {
    return ResponseEntity.ok(bookService.saveBookCover(bookId, file));
  }

  /**
   * Удаление книги с указанным идентификатором.
   *
   * @param bookId Идентификатор книги.
   * @return Объект {@link ResponseEntity} без тела с кодом успешного выполнения.
   */
  @DeleteMapping("/{bookId}")
  public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
    bookService.deleteBookById(bookId);
    return ResponseEntity.ok().build();
  }

  /**
   * Экспортирует все книги в формате Excel.
   *
   * @return Объект {@link ResponseEntity} с байтами файла Excel и соответствующими заголовками.
   */
  @GetMapping("/to-excel")
  public ResponseEntity<byte[]> export() {
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.xlsx")
        .body(bookService.exportBooksToExcel());
  }
}