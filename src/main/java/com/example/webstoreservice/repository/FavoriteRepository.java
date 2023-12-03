package com.example.webstoreservice.repository;

import com.example.webstoreservice.model.entity.Favorite;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

  List<Favorite> findByUserUuid(UUID userUuid);

  Optional<Favorite> findByUserUuidAndBookId(UUID userUuid, Long bookId);
}