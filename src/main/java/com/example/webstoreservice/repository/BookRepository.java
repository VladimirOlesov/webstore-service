package com.example.webstoreservice.repository;

import com.example.webstoreservice.model.entity.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

  Optional<Book> findByISBN(String isbn);
}