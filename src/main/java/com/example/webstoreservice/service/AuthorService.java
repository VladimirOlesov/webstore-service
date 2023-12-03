package com.example.webstoreservice.service;

import com.example.webstoreservice.model.dto.AuthorDto;
import com.example.webstoreservice.model.enums.SortDirection;
import java.util.List;

public interface AuthorService {

  List<AuthorDto> getAuthors(String authorName, SortDirection sortDirection);
}