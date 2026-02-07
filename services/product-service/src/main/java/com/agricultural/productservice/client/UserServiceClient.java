package com.agricultural.productservice.client;

import com.agricultural.productservice.client.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-service", path = "/api/users")
public interface UserServiceClient {
    
    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
    
    @GetMapping("/username/{username}")
    UserDTO getUserByUsername(@PathVariable("username") String username);
}