package com.example.webstoreservice.service.impl;

import com.example.webstoreservice.model.dto.GenreDto;
import com.example.webstoreservice.model.entity.Genre;
import com.example.webstoreservice.model.entity.Genre_;
import com.example.webstoreservice.model.enums.SortDirection;
import com.example.webstoreservice.model.mapper.GenreMapper;
import com.example.webstoreservice.service.GenreService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

  private final GenreMapper genreMapper;

  private final EntityManager entityManager;

  @Override
  public List<GenreDto> getGenres(String genreName, SortDirection sortDirection) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Genre> cq = cb.createQuery(Genre.class);

    Root<Genre> genre = cq.from(Genre.class);
    List<Predicate> predicates = new ArrayList<>();

    if (StringUtils.hasText(genreName)) {
      predicates.add(cb.like(cb.lower(genre.get(Genre_.genreName)),
          MessageFormat.format("%{0}%", genreName.toLowerCase())));
    }

    cq.where(predicates.toArray(Predicate[]::new));

    if (sortDirection == SortDirection.DESC) {
      cq.orderBy(cb.desc(genre.get(Genre_.genreName)));
    } else {
      cq.orderBy(cb.asc(genre.get(Genre_.genreName)));
    }

    TypedQuery<Genre> query = entityManager.createQuery(cq);
    List<Genre> genres = query.getResultList();

    if (genres.isEmpty()) {
      throw new EntityNotFoundException("Жанры не найдены");
    }

    return genres.stream()
        .map(genreMapper::genreToDto)
        .collect(Collectors.toList());
  }
}