package com.example.webstoreservice.service;

import com.example.webstoreservice.model.dto.GenreDto;
import com.example.webstoreservice.model.enums.SortDirection;
import java.util.List;

public interface GenreService {

  List<GenreDto> getGenres(String genreName, SortDirection sortDirection);
}