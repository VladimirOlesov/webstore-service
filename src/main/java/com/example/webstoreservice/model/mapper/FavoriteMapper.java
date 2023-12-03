package com.example.webstoreservice.model.mapper;

import com.example.webstoreservice.model.dto.FavoriteIdDto;
import com.example.webstoreservice.model.entity.FavoriteId;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

  FavoriteIdDto favoriteIdToDto(FavoriteId favoriteId);
}