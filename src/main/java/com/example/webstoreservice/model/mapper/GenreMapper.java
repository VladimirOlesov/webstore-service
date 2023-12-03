package com.example.webstoreservice.model.mapper;

import com.example.webstoreservice.model.dto.GenreDto;
import com.example.webstoreservice.model.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GenreMapper {

  @Mapping(source = "id", target = "genreId")
  GenreDto genreToDto(Genre genre);
}