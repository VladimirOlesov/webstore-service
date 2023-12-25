package com.example.webstoreservice.service.impl;

import com.example.webstoreservice.model.dto.BookDto;
import com.example.webstoreservice.model.dto.FavoriteIdDto;
import com.example.webstoreservice.model.dto.UserDto;
import com.example.webstoreservice.model.entity.Book;
import com.example.webstoreservice.model.entity.Favorite;
import com.example.webstoreservice.model.entity.FavoriteId;
import com.example.webstoreservice.model.mapper.BookMapper;
import com.example.webstoreservice.model.mapper.FavoriteMapper;
import com.example.webstoreservice.repository.BookRepository;
import com.example.webstoreservice.repository.FavoriteRepository;
import com.example.webstoreservice.service.FavoriteService;
import com.example.webstoreservice.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса {@link FavoriteService} для работы с избранными книгами.
 */
@RequiredArgsConstructor
@Service
public class FavoriteServiceImpl implements FavoriteService {

  private final FavoriteRepository favoriteRepository;

  private final BookMapper bookMapper;

  private final BookRepository bookRepository;

  private final FavoriteMapper favoriteMapper;

  private final UserService userService;

  /**
   * Удаление книги из списка избранного пользователя.
   *
   * @param bookId Идентификатор книги.
   * @throws EntityNotFoundException, если книга не найдена в избранном пользователя.
   * @throws EntityNotFoundException, если пользователь не аутентифицирован.
   */
  @Override
  @Transactional
  public void removeFromFavorites(Long bookId) {
    UserDto user = userService.getAuthenticatedUser();

    Favorite favorite = favoriteRepository.findByUserUuidAndBookId(user.userUuid(), bookId)
        .orElseThrow(() -> new EntityNotFoundException("Книга не найдена в избранном"));

    favoriteRepository.delete(favorite);
  }

  /**
   * Добавление книги в список избранного пользователя.
   *
   * @param bookId Идентификатор книги.
   * @return Объект {@link FavoriteIdDto} с информацией о добавленной книге в избранное.
   * @throws EntityNotFoundException, если книга не найдена.
   * @throws EntityNotFoundException, если пользователь не аутентифицирован.
   */
  @Override
  @Transactional
  public FavoriteIdDto addToFavorites(Long bookId) {
    UserDto user = userService.getAuthenticatedUser();

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new EntityNotFoundException("Книга не найдена"));

    Favorite favorite = favoriteRepository.save(Favorite.builder()
        .id(new FavoriteId(user.userUuid(), book.getId()))
        .userUuid(user.userUuid())
        .book(book)
        .build());
    return favoriteMapper.favoriteIdToDto(favorite.getId());
  }

  /**
   * Получение списка избранных книг пользователя.
   *
   * @return Список объектов {@link BookDto} избранных книг пользователя.
   * @throws EntityNotFoundException, если в избранном пользователя нет книг.
   * @throws EntityNotFoundException, если пользователь не аутентифицирован.
   */
  @Override
  public List<BookDto> getFavoriteBooks() {
    UserDto user = userService.getAuthenticatedUser();

    List<Favorite> favoriteBooks = favoriteRepository.findByUserUuid(user.userUuid());

    if (favoriteBooks.isEmpty()) {
      throw new EntityNotFoundException("В избранном нет книг");
    }

    return favoriteBooks.stream()
        .map(favorite -> bookMapper.bookToBookDto(favorite.getBook()))
        .toList();
  }
}