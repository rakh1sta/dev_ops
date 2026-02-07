package com.agricultural.statisticsservice.service;

import com.agricultural.statisticsservice.dto.DailyReportDTO;
import com.agricultural.statisticsservice.client.dto.ProductDTO;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    
    // Daily Report Management
    DailyReportDTO createDailyReport(LocalDate date);
    
    DailyReportDTO getDailyReportByDate(LocalDate date);
    
    List<DailyReportDTO> getAllDailyReports();
    
    // Scheduled Report Generation
    void generateDailyReport();
    
    // Market Trends Analysis
    List<ProductDTO> getTopSellingProducts(int limit);
    
    // System Health & Metrics
    Object getSystemHealthMetrics();
    
    // Transaction Monitoring
    Object getTransactionMetrics();
}