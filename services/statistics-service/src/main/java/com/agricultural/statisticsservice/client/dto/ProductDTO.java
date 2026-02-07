package com.agricultural.statisticsservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal currentPrice;
    private Long categoryId;
    private String categoryName;
}