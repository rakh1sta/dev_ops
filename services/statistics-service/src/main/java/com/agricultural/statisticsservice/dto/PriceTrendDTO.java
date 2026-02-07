package com.agricultural.statisticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceTrendDTO {
    
    private Long productId;
    private String productName;
    private String category;
    private List<PricePoint> pricePoints;
    private String trendDirection; // UP, DOWN, STABLE
    private BigDecimal currentPrice;
    private BigDecimal priceChangePercentage;
    private LocalDate analysisDate;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricePoint {
        private LocalDate date;
        private BigDecimal price;
    }
}