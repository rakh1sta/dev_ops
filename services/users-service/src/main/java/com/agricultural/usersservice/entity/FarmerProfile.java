package com.agricultural.usersservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "farmer_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "farm_area_hectares")
    private Double farmAreaHectares;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "farm_description")
    private String farmDescription;
    
    @Column(name = "contact_number")
    private String contactNumber;
}