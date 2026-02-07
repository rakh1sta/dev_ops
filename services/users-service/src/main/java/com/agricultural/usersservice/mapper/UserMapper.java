package com.agricultural.usersservice.mapper;

import com.agricultural.usersservice.dto.UserDTO;
import com.agricultural.usersservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {FarmerProfileMapper.class})
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    @Mapping(target = "farmerProfile", source = "farmerProfile")
    UserDTO toDto(User user);
    
    @Mapping(target = "farmerProfile", source = "farmerProfile")
    User toEntity(UserDTO userDTO);
}