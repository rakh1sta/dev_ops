package com.agricultural.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryDTO {
    
    private Long id;
    private Long productId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private LocalDateTime changeDate;
    private LocalDateTime createdAt;
}