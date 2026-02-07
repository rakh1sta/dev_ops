package com.agricultural.statisticsservice.service;

import com.agricultural.statisticsservice.dto.PriceTrendDTO;

import java.time.LocalDate;
import java.util.List;

public interface PriceTrendService {
    
    List<PriceTrendDTO> getHistoricalPriceTrends(Long productId, LocalDate startDate, LocalDate endDate);
    
    List<PriceTrendDTO> getRecentPriceChanges(int days);
    
    List<PriceTrendDTO> getTopPriceChangers(int limit, String direction); // UP or DOWN
    
    PriceTrendDTO getPriceTrendForProduct(Long productId);
    
    List<PriceTrendDTO> getSeasonalPriceTrends(Long productId, String season);
}