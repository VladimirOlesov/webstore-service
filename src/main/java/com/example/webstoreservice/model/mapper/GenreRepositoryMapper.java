package com.example.webstoreservice.model.mapper;

import com.example.webstoreservice.model.entity.Genre;
import com.example.webstoreservice.repository.GenreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GenreRepositoryMapper {

  private final GenreRepository genreRepository;

  @CustomMapping
  public Genre mapToGenre(Long genreId) {
    return genreRepository.findById(genreId)
        .orElseThrow(() -> new EntityNotFoundException("Жанр не найден"));
  }
}