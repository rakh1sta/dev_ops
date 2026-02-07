package com.agricultural.statisticsservice.controller;

import com.agricultural.statisticsservice.dto.DailyReportDTO;
import com.agricultural.statisticsservice.client.dto.ProductDTO;
import com.agricultural.statisticsservice.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @GetMapping("/daily-reports")
    public ResponseEntity<List<DailyReportDTO>> getAllDailyReports() {
        List<DailyReportDTO> reports = statisticsService.getAllDailyReports();
        return ResponseEntity.ok(reports);
    }
    
    @GetMapping("/daily-reports/{date}")
    public ResponseEntity<DailyReportDTO> getDailyReportByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyReportDTO report = statisticsService.getDailyReportByDate(date);
        return ResponseEntity.ok(report);
    }
    
    @PostMapping("/daily-reports/generate")
    public ResponseEntity<DailyReportDTO> generateDailyReport(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        DailyReportDTO report = statisticsService.createDailyReport(date);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/top-products")
    public ResponseEntity<List<ProductDTO>> getTopSellingProducts(@RequestParam(defaultValue = "10") int limit) {
        List<ProductDTO> products = statisticsService.getTopSellingProducts(limit);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/system-health")
    public ResponseEntity<Object> getSystemHealthMetrics() {
        Object metrics = statisticsService.getSystemHealthMetrics();
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/transaction-metrics")
    public ResponseEntity<Object> getTransactionMetrics() {
        Object metrics = statisticsService.getTransactionMetrics();
        return ResponseEntity.ok(metrics);
    }
}