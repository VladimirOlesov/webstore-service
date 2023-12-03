package com.example.webstoreservice.repository;

import com.example.webstoreservice.model.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {

}