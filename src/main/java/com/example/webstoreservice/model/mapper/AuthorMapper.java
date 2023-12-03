package com.example.webstoreservice.model.mapper;

import com.example.webstoreservice.model.dto.AuthorDto;
import com.example.webstoreservice.model.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

  @Mapping(source = "id", target = "authorId")
  AuthorDto authorToDto(Author author);
}