package com.agricultural.usersservice.service.impl;

import com.agricultural.usersservice.dto.FarmerProfileDTO;
import com.agricultural.usersservice.entity.FarmerProfile;
import com.agricultural.usersservice.entity.User;
import com.agricultural.usersservice.mapper.FarmerProfileMapper;
import com.agricultural.usersservice.repository.FarmerProfileRepository;
import com.agricultural.usersservice.repository.UserRepository;
import com.agricultural.usersservice.service.FarmerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FarmerProfileServiceImpl implements FarmerProfileService {
    
    private final FarmerProfileRepository farmerProfileRepository;
    private final UserRepository userRepository;
    private final FarmerProfileMapper farmerProfileMapper;
    
    @Override
    public FarmerProfileDTO createFarmerProfile(Long userId, FarmerProfileDTO farmerProfileDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        FarmerProfile farmerProfile = farmerProfileMapper.toEntity(farmerProfileDTO);
        farmerProfile.setUser(user);
        
        FarmerProfile savedProfile = farmerProfileRepository.save(farmerProfile);
        
        // Also update the user with the farmer profile
        user.setFarmerProfile(savedProfile);
        userRepository.save(user);
        
        return farmerProfileMapper.toDto(savedProfile);
    }
    
    @Override
    public FarmerProfileDTO getFarmerProfileByUserId(Long userId) {
        FarmerProfile profile = farmerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found for user id: " + userId));
        
        return farmerProfileMapper.toDto(profile);
    }
    
    @Override
    public FarmerProfileDTO updateFarmerProfile(Long userId, FarmerProfileDTO farmerProfileDTO) {
        FarmerProfile existingProfile = farmerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found for user id: " + userId));
        
        // Update fields
        existingProfile.setFarmAreaHectares(farmerProfileDTO.getFarmAreaHectares());
        existingProfile.setLocation(farmerProfileDTO.getLocation());
        existingProfile.setFarmDescription(farmerProfileDTO.getFarmDescription());
        existingProfile.setContactNumber(farmerProfileDTO.getContactNumber());
        
        FarmerProfile updatedProfile = farmerProfileRepository.save(existingProfile);
        return farmerProfileMapper.toDto(updatedProfile);
    }
    
    @Override
    public void deleteFarmerProfile(Long userId) {
        FarmerProfile profile = farmerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found for user id: " + userId));
        
        farmerProfileRepository.delete(profile);
    }
}