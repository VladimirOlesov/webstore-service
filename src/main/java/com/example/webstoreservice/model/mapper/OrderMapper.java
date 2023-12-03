package com.example.webstoreservice.model.mapper;

import com.example.webstoreservice.model.dto.OrderDto;
import com.example.webstoreservice.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  @Mapping(source = "id", target = "orderId")
  OrderDto toOrderDto(Order order);
}