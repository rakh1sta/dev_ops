package com.agricultural.productservice.security;

import com.agricultural.productservice.client.UserServiceClient;
import com.agricultural.productservice.client.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class SecurityService {
    
    private final UserServiceClient userServiceClient;
    
    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
    }
    
    public boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }
    
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            // In a real scenario, you might store user ID in JWT claims
            // For now, we'll need to get user by username
            return userServiceClient.getUserByUsername(authentication.getName());
        }
        return null;
    }
    
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
    
    public boolean isFarmer() {
        return hasRole("FARMER");
    }
    
    public boolean isSupplier() {
        return hasRole("SUPPLIER");
    }
    
    public boolean isDistributor() {
        return hasRole("DISTRIBUTOR");
    }
}