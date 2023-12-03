package com.example.webstoreservice.model.mapper;

import com.example.webstoreservice.model.entity.Author;
import com.example.webstoreservice.repository.AuthorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthorRepositoryMapper {

  private final AuthorRepository authorRepository;

  @CustomMapping
  public Author mapToAuthor(Long authorId) {
    return authorRepository.findById(authorId)
        .orElseThrow(() -> new EntityNotFoundException("Автор не найден"));
  }
}