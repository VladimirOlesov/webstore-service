package com.example.webstoreservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static com.example.webstoreservice.model.dto.AuthorDto.Fields.authorName;
import static com.example.webstoreservice.model.dto.GenreDto.Fields.genreName;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.webstoreservice.IntegrationTestBase;
import com.example.webstoreservice.model.dto.AuthorDto;
import com.example.webstoreservice.model.dto.BookDto;
import com.example.webstoreservice.model.dto.GenreDto;
import com.example.webstoreservice.model.entity.Author;
import com.example.webstoreservice.model.entity.Book;
import com.example.webstoreservice.model.entity.Genre;
import com.example.webstoreservice.repository.AuthorRepository;
import com.example.webstoreservice.repository.GenreRepository;
import com.example.webstoreservice.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@RequiredArgsConstructor
@WithMockUser(username = "testUser", authorities = {"USER", "ADMIN"})
class AdminBookControllerIT extends IntegrationTestBase {

  private final MockMvc mockMvc;

  private final ObjectMapper objectMapper;

  private final BookService bookService;

  private final AuthorRepository authorRepository;

  private final GenreRepository genreRepository;

  @Test
  void createBook() throws Exception {

    Author author = authorRepository.save(
        Author.builder()
            .authorName(authorName)
            .build()
    );

    Genre genre = genreRepository.save(
        Genre.builder()
            .genreName(genreName)
            .build()
    );

    BookDto bookDto = BookDto.builder()
        .title("title")
        .author(
            AuthorDto.builder()
                .authorId(author.getId())
                .authorName(authorName)
                .build()
        )
        .genre(
            GenreDto.builder()
                .genreId(genre.getId())
                .genreName(genreName)
                .build()
        )
        .publicationYear(2000)
        .price(new BigDecimal("500.00"))
        .ISBN("ISBN")
        .pageCount(500)
        .ageRating(16)
        .coverPath("coverPath")
        .deleted(false)
        .build();

    MvcResult mvcResult = mockMvc.perform(post("/admin/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookId").exists())
        .andExpect(jsonPath("$.title", is(bookDto.title())))
        .andExpect(jsonPath("$.author.authorName", is(bookDto.author().authorName())))
        .andExpect(jsonPath("$.genre.genreName", is(bookDto.genre().genreName())))
        .andExpect(jsonPath("$.publicationYear", is(bookDto.publicationYear())))
        .andExpect(jsonPath("$.price", is(bookDto.price().doubleValue())))
        .andExpect(jsonPath("$.ISBN", is(bookDto.ISBN())))
        .andExpect(jsonPath("$.pageCount", is(bookDto.pageCount())))
        .andExpect(jsonPath("$.ageRating", is(bookDto.ageRating())))
        .andExpect(jsonPath("$.coverPath", is(bookDto.coverPath())))
        .andExpect(jsonPath("$.deleted", is(bookDto.deleted())))
        .andReturn();

    BookDto returnedBook = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
        BookDto.class);

    Book savedBook = bookService.getBookById(returnedBook.bookId());

    assertThat(savedBook.getId()).isEqualTo(returnedBook.bookId());
    assertThat(savedBook.getTitle()).isEqualTo(returnedBook.title());
    assertThat(savedBook.getAuthor().getAuthorName()).isEqualTo(returnedBook.author().authorName());
    assertThat(savedBook.getGenre().getGenreName()).isEqualTo(returnedBook.genre().genreName());
    assertThat(savedBook.getPublicationYear()).isEqualTo(returnedBook.publicationYear());
    assertThat(savedBook.getPrice()).isEqualTo(returnedBook.price());
    assertThat(savedBook.getISBN()).isEqualTo(returnedBook.ISBN());
    assertThat(savedBook.getPageCount()).isEqualTo(returnedBook.pageCount());
    assertThat(savedBook.getAgeRating()).isEqualTo(returnedBook.ageRating());
    assertThat(savedBook.getCoverPath()).isEqualTo(returnedBook.coverPath());
    assertThat(savedBook.getDeleted()).isEqualTo(returnedBook.deleted());
  }
}