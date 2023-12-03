package com.example.webstoreservice.repository;

import com.example.webstoreservice.model.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {

}