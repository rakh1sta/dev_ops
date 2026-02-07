package com.agricultural.productservice.mapper;

import com.agricultural.productservice.dto.PriceHistoryDTO;
import com.agricultural.productservice.entity.PriceHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PriceHistoryMapper {
    
    PriceHistoryMapper INSTANCE = Mappers.getMapper(PriceHistoryMapper.class);
    
    @Mapping(source = "product.id", target = "productId")
    PriceHistoryDTO toDto(PriceHistory priceHistory);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PriceHistory toEntity(PriceHistoryDTO priceHistoryDTO);
    
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(PriceHistoryDTO priceHistoryDTO, @MappingTarget PriceHistory priceHistory);
}