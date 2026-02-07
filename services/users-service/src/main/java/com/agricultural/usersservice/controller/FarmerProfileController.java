package com.agricultural.usersservice.controller;

import com.agricultural.usersservice.dto.FarmerProfileDTO;
import com.agricultural.usersservice.service.FarmerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farmer-profiles")
@RequiredArgsConstructor
public class FarmerProfileController {
    
    private final FarmerProfileService farmerProfileService;
    
    @PostMapping("/user/{userId}")
    public ResponseEntity<FarmerProfileDTO> createFarmerProfile(@PathVariable Long userId, @RequestBody FarmerProfileDTO farmerProfileDTO) {
        var profile = farmerProfileService.createFarmerProfile(userId, farmerProfileDTO);
        return ResponseEntity.ok(profile);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<FarmerProfileDTO> getFarmerProfileByUserId(@PathVariable Long userId) {
        var profile = farmerProfileService.getFarmerProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping("/user/{userId}")
    public ResponseEntity<FarmerProfileDTO> updateFarmerProfile(@PathVariable Long userId, @RequestBody FarmerProfileDTO farmerProfileDTO) {
        var profile = farmerProfileService.updateFarmerProfile(userId, farmerProfileDTO);
        return ResponseEntity.ok(profile);
    }
    
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteFarmerProfile(@PathVariable Long userId) {
        farmerProfileService.deleteFarmerProfile(userId);
        return ResponseEntity.noContent().build();
    }
}