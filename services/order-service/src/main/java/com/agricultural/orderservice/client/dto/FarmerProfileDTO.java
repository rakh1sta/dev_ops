package com.agricultural.orderservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerProfileDTO {
    
    private Long id;
    private Double farmAreaHectares;
    private String location;
    private String farmDescription;
    private String contactNumber;
}