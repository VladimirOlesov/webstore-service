package com.example.webstoreservice.controller;

import static com.example.webstoreservice.model.dto.AuthorDto.Fields.authorName;
import static com.example.webstoreservice.model.dto.GenreDto.Fields.genreName;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.webstoreservice.IntegrationTestBase;
import com.example.webstoreservice.model.entity.Author;
import com.example.webstoreservice.model.entity.Book;
import com.example.webstoreservice.model.entity.Genre;
import com.example.webstoreservice.repository.AuthorRepository;
import com.example.webstoreservice.repository.BookRepository;
import com.example.webstoreservice.repository.GenreRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@AutoConfigureMockMvc
@RequiredArgsConstructor
class BookControllerIT extends IntegrationTestBase {

  private final MockMvc mockMvc;

  private final BookRepository bookRepository;

  private final AuthorRepository authorRepository;

  private final GenreRepository genreRepository;

  private static final Long NON_EXISTENT_BOOK_ID = 999L;

  @Test
  void successfulGetBookById() throws Exception {
    Book book = bookRepository.save(
        Book.builder()
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
            .publicationYear(2000)
            .price(new BigDecimal("500.00"))
            .ISBN("ISBN")
            .pageCount(500)
            .ageRating(16)
            .coverPath("coverPath")
            .deleted(false)
            .build()
    );

    mockMvc.perform(get("/books/" + book.getId())

            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is(book.getTitle())))
        .andExpect(jsonPath("$.author.authorName", is(book.getAuthor().getAuthorName())))
        .andExpect(jsonPath("$.genre.genreName", is(book.getGenre().getGenreName())))
        .andExpect(jsonPath("$.publicationYear", is(book.getPublicationYear())))
        .andExpect(jsonPath("$.price", is(book.getPrice().doubleValue())))
        .andExpect(jsonPath("$.ISBN", is(book.getISBN())))
        .andExpect(jsonPath("$.pageCount", is(book.getPageCount())))
        .andExpect(jsonPath("$.ageRating", is(book.getAgeRating())))
        .andExpect(jsonPath("$.coverPath", is(book.getCoverPath())))
        .andExpect(jsonPath("$.deleted", is(book.getDeleted())));
  }

  @Test
  void getNonExistentBookById() throws Exception {

    mockMvc.perform(get("/books/" + NON_EXISTENT_BOOK_ID)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", containsString("Книга не найдена")));
  }
}