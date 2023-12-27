package com.example.webstoreservice.model.mapper;

import com.example.commoncode.model.mapper.CustomMapping;
import com.example.webstoreservice.model.dto.BookDto;
import com.example.webstoreservice.model.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",
    uses = {AuthorMapper.class, GenreMapper.class, AuthorRepositoryMapper.class,
        GenreRepositoryMapper.class})
public interface BookMapper {

  @Mapping(source = "author.authorId", target = "author", qualifiedBy = CustomMapping.class)
  @Mapping(source = "genre.genreId", target = "genre", qualifiedBy = CustomMapping.class)
  Book bookDtoToBook(BookDto bookDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "author.authorId", target = "author", qualifiedBy = CustomMapping.class)
  @Mapping(source = "genre.genreId", target = "genre", qualifiedBy = CustomMapping.class)
  void updateBookFromDto(BookDto bookDto, @MappingTarget Book book);

  @Mapping(source = "id", target = "bookId")
  BookDto bookToBookDto(Book book);
}