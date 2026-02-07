package com.agricultural.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {
    
    private Long productId;
    private String productName;
    private String category;
    private Integer quantity;
    private BigDecimal currentPrice;
    private BigDecimal oldPrice;
    private String changeType; // PRICE_CHANGE, QUANTITY_CHANGE, BOTH
    private LocalDateTime timestamp;
}