package com.agricultural.usersservice.dto;

import com.agricultural.usersservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    private String username;
    private String email;
    private String password;
    private Set<Role> roles;
    private FarmerProfileDTO farmerProfile;
}