package com.example.webstoreservice.repository;

import static com.example.webstoreservice.model.entity.Author.Fields.authorName;
import static com.example.webstoreservice.model.entity.BaseEntity.Fields.id;
import static com.example.webstoreservice.model.entity.Book.Fields.ISBN;
import static com.example.webstoreservice.model.entity.Book.Fields.author;
import static com.example.webstoreservice.model.entity.Book.Fields.deleted;
import static com.example.webstoreservice.model.entity.Book.Fields.genre;
import static com.example.webstoreservice.model.entity.Book.Fields.title;
import static com.example.webstoreservice.model.entity.Genre.Fields.genreName;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.webstoreservice.IntegrationTestBase;
import com.example.webstoreservice.model.entity.Author;
import com.example.webstoreservice.model.entity.Book;
import com.example.webstoreservice.model.entity.Genre;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
public class BookRepositoryIT extends IntegrationTestBase {

  private final BookRepository bookRepository;

  private final AuthorRepository authorRepository;

  private final GenreRepository genreRepository;

  @Test
  void SaveBook() {

    Book book = Book.builder()
        .title("title")
        .author(authorRepository.save(
            Author.builder()
                .authorName(authorName)
                .build())
        )
        .genre(genreRepository.save(
            Genre.builder()
                .genreName(genreName)
                .build())
        )
        .ISBN("ISBN")
        .deleted(false)
        .build();

    Book savedBook = bookRepository.save(book);

    Book foundBook = bookRepository.findById(savedBook.getId()).orElse(null);

    assertThat(foundBook)
        .isNotNull()
        .hasFieldOrPropertyWithValue(id, savedBook.getId())
        .hasFieldOrPropertyWithValue(title, "title")
        .hasFieldOrPropertyWithValue(author, book.getAuthor())
        .hasFieldOrPropertyWithValue(genre, book.getGenre())
        .hasFieldOrPropertyWithValue(ISBN, "ISBN")
        .hasFieldOrPropertyWithValue(deleted, false)
        .isEqualTo(book);
  }
}