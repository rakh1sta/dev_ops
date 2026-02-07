package com.agricultural.orderservice.mapper;

import com.agricultural.orderservice.dto.TransactionDTO;
import com.agricultural.orderservice.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
    
    @Mapping(source = "paymentStatus", target = "paymentStatus")
    TransactionDTO toDto(Transaction transaction);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Transaction toEntity(TransactionDTO transactionDTO);
    
    @Mapping(source = "paymentStatus", target = "paymentStatus")
    void updateEntityFromDto(TransactionDTO transactionDTO, @MappingTarget Transaction transaction);
}