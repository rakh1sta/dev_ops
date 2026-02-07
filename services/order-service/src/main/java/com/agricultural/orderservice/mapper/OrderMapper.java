package com.agricultural.orderservice.mapper;

import com.agricultural.orderservice.dto.OrderDTO;
import com.agricultural.orderservice.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, TransactionMapper.class})
public interface OrderMapper {
    
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    
    @Mapping(source = "status", target = "status")
    OrderDTO toDto(Order order);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(OrderDTO orderDTO);
    
    @Mapping(source = "status", target = "status")
    void updateEntityFromDto(OrderDTO orderDTO, @MappingTarget Order order);
}