package com.agricultural.statisticsservice.controller;

import com.agricultural.statisticsservice.dto.PriceTrendDTO;
import com.agricultural.statisticsservice.service.PriceTrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class PriceTrendController {
    
    private final PriceTrendService priceTrendService;
    
    @GetMapping("/price-trends/{productId}")
    public ResponseEntity<PriceTrendDTO> getPriceTrendForProduct(@PathVariable Long productId) {
        PriceTrendDTO trend = priceTrendService.getPriceTrendForProduct(productId);
        return ResponseEntity.ok(trend);
    }
    
    @GetMapping("/price-trends/historical/{productId}")
    public ResponseEntity<List<PriceTrendDTO>> getHistoricalPriceTrends(
            @PathVariable Long productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PriceTrendDTO> trends = priceTrendService.getHistoricalPriceTrends(productId, startDate, endDate);
        return ResponseEntity.ok(trends);
    }
    
    @GetMapping("/price-trends/recent")
    public ResponseEntity<List<PriceTrendDTO>> getRecentPriceChanges(
            @RequestParam(defaultValue = "7") int days) {
        List<PriceTrendDTO> trends = priceTrendService.getRecentPriceChanges(days);
        return ResponseEntity.ok(trends);
    }
    
    @GetMapping("/price-trends/top-changers")
    public ResponseEntity<List<PriceTrendDTO>> getTopPriceChangers(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "BOTH") String direction) {
        List<PriceTrendDTO> trends = priceTrendService.getTopPriceChangers(limit, direction);
        return ResponseEntity.ok(trends);
    }
    
    @GetMapping("/price-trends/seasonal/{productId}")
    public ResponseEntity<List<PriceTrendDTO>> getSeasonalPriceTrends(
            @PathVariable Long productId,
            @RequestParam String season) {
        List<PriceTrendDTO> trends = priceTrendService.getSeasonalPriceTrends(productId, season);
        return ResponseEntity.ok(trends);
    }
}