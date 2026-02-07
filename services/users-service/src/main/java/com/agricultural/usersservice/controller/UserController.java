package com.agricultural.usersservice.controller;

import com.agricultural.usersservice.dto.AuthRequest;
import com.agricultural.usersservice.dto.RegisterRequest;
import com.agricultural.usersservice.dto.UserDTO;
import com.agricultural.usersservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest registerRequest) {
        var user = userService.registerUser(registerRequest);
        return ResponseEntity.ok(toUserDTO(user));
    }
    
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody AuthRequest authRequest) {
        var user = userService.authenticateUser(authRequest);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        var user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        var users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        var updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> findByUsername(@PathVariable String username) {
        var user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }
    
    private UserDTO toUserDTO(com.agricultural.usersservice.entity.User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRoles(user.getRoles());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        if (user.getFarmerProfile() != null) {
            com.agricultural.usersservice.dto.FarmerProfileDTO farmerProfileDTO = new com.agricultural.usersservice.dto.FarmerProfileDTO();
            farmerProfileDTO.setId(user.getFarmerProfile().getId());
            farmerProfileDTO.setFarmAreaHectares(user.getFarmerProfile().getFarmAreaHectares());
            farmerProfileDTO.setLocation(user.getFarmerProfile().getLocation());
            farmerProfileDTO.setFarmDescription(user.getFarmerProfile().getFarmDescription());
            farmerProfileDTO.setContactNumber(user.getFarmerProfile().getContactNumber());
            userDTO.setFarmerProfile(farmerProfileDTO);
        }
        return userDTO;
    }
}