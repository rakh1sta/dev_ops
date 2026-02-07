package com.agricultural.usersservice.mapper;

import com.agricultural.usersservice.dto.FarmerProfileDTO;
import com.agricultural.usersservice.entity.FarmerProfile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FarmerProfileMapper {
    
    FarmerProfileMapper INSTANCE = Mappers.getMapper(FarmerProfileMapper.class);
    
    FarmerProfileDTO toDto(FarmerProfile farmerProfile);
    
    FarmerProfile toEntity(FarmerProfileDTO farmerProfileDTO);
}