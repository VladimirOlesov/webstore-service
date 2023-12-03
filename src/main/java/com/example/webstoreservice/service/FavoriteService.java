package com.example.webstoreservice.service;

import com.example.webstoreservice.model.dto.BookDto;
import com.example.webstoreservice.model.dto.FavoriteIdDto;
import java.util.List;

public interface FavoriteService {

  void removeFromFavorites(Long favoriteId);

  FavoriteIdDto addToFavorites(Long bookId);

  List<BookDto> getFavoriteBooks();
}