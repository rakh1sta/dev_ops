package com.agricultural.orderservice.mapper;

import com.agricultural.orderservice.dto.OrderItemDTO;
import com.agricultural.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    
    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);
    
    OrderItemDTO toDto(OrderItem orderItem);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemDTO orderItemDTO);
    
    void updateEntityFromDto(OrderItemDTO orderItemDTO, @MappingTarget OrderItem orderItem);
}