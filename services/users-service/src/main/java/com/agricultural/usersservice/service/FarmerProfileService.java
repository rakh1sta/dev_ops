package com.agricultural.usersservice.service;

import com.agricultural.usersservice.dto.FarmerProfileDTO;

public interface FarmerProfileService {
    
    FarmerProfileDTO createFarmerProfile(Long userId, FarmerProfileDTO farmerProfileDTO);
    
    FarmerProfileDTO getFarmerProfileByUserId(Long userId);
    
    FarmerProfileDTO updateFarmerProfile(Long userId, FarmerProfileDTO farmerProfileDTO);
    
    void deleteFarmerProfile(Long userId);
}