package com.agricultural.usersservice.service;

import com.agricultural.usersservice.dto.AuthRequest;
import com.agricultural.usersservice.dto.RegisterRequest;
import com.agricultural.usersservice.dto.UserDTO;
import com.agricultural.usersservice.entity.User;

import java.util.List;

public interface UserService {
    
    User registerUser(RegisterRequest registerRequest);
    
    UserDTO authenticateUser(AuthRequest authRequest);
    
    UserDTO getUserById(Long id);
    
    List<UserDTO> getAllUsers();
    
    UserDTO updateUser(Long id, UserDTO userDTO);
    
    void deleteUser(Long id);
    
    UserDTO findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}