package com.agricultural.statisticsservice.service.impl;

import com.agricultural.statisticsservice.client.OrderServiceClient;
import com.agricultural.statisticsservice.client.ProductServiceClient;
import com.agricultural.statisticsservice.client.UserServiceClient;
import com.agricultural.statisticsservice.client.dto.OrderDTO;
import com.agricultural.statisticsservice.client.dto.ProductDTO;
import com.agricultural.statisticsservice.dto.DailyReportDTO;
import com.agricultural.statisticsservice.entity.DailyReport;
import com.agricultural.statisticsservice.mapper.DailyReportMapper;
import com.agricultural.statisticsservice.repository.DailyReportRepository;
import com.agricultural.statisticsservice.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StatisticsServiceImpl implements StatisticsService {
    
    private final DailyReportRepository dailyReportRepository;
    private final OrderServiceClient orderServiceClient;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;
    private final DailyReportMapper dailyReportMapper;
    
    @Override
    public DailyReportDTO createDailyReport(LocalDate date) {
        // Check if report already exists for this date
        Optional<DailyReport> existingReport = dailyReportRepository.findByReportDate(date);
        if (existingReport.isPresent()) {
            return dailyReportMapper.toDto(existingReport.get());
        }
        
        // Calculate report metrics
        List<OrderDTO> orders = orderServiceClient.getOrdersByDateRange(date, date);
        
        Long totalOrders = (long) orders.size();
        BigDecimal totalRevenue = orders.stream()
                .map(OrderDTO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Count unique customers who placed orders today
        Set<Long> activeCustomers = orders.stream()
                .map(OrderDTO::getCustomerId)
                .collect(Collectors.toSet());
        Long activeUsers = (long) activeCustomers.size();
        
        // Create and save the report
        DailyReport report = new DailyReport();
        report.setReportDate(date);
        report.setTotalOrders(totalOrders);
        report.setTotalRevenue(totalRevenue);
        report.setActiveUsers(activeUsers);
        
        DailyReport savedReport = dailyReportRepository.save(report);
        return dailyReportMapper.toDto(savedReport);
    }
    
    @Override
    public DailyReportDTO getDailyReportByDate(LocalDate date) {
        DailyReport report = dailyReportRepository.findByReportDate(date)
                .orElseThrow(() -> new RuntimeException("Daily report not found for date: " + date));
        return dailyReportMapper.toDto(report);
    }
    
    @Override
    public List<DailyReportDTO> getAllDailyReports() {
        return dailyReportRepository.findAll().stream()
                .map(dailyReportMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public void generateDailyReport() {
        LocalDate today = LocalDate.now();
        createDailyReport(today);
    }
    
    @Override
    public List<ProductDTO> getTopSellingProducts(int limit) {
        // Get all products
        List<ProductDTO> allProducts = productServiceClient.getAllProducts();
        
        // In a real scenario, we would need to get order items to determine which products were in each order
        // For this example, we'll simulate by returning the products with highest demand based on orders
        
        // Create a map to count how many times each product was ordered
        Map<Long, Integer> productSalesCount = new HashMap<>();
        
        // Simulate getting product IDs from order items (in a real scenario, we'd need to call an endpoint to get order items)
        List<OrderDTO> allOrders = orderServiceClient.getAllOrders();
        
        // For simulation purposes, we'll assign random sales counts to products
        for (int i = 0; i < Math.min(allProducts.size(), allOrders.size()); i++) {
            Long productId = allProducts.get(i).getId();
            productSalesCount.put(productId, allOrders.size() - i); // Higher index = fewer sales
        }
        
        // Sort products by sales count and return top N
        return allProducts.stream()
                .sorted((p1, p2) -> {
                    Integer count1 = productSalesCount.getOrDefault(p1.getId(), 0);
                    Integer count2 = productSalesCount.getOrDefault(p2.getId(), 0);
                    return count2.compareTo(count1); // Descending order
                })
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public Object getSystemHealthMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("timestamp", LocalDateTime.now());
        metrics.put("uptime", "System uptime information");
        metrics.put("diskSpace", "Disk space usage");
        metrics.put("memory", "Memory usage information");
        metrics.put("activeUsers", userServiceClient.getAllUsers().size());
        metrics.put("totalOrders", orderServiceClient.getAllOrders().size());
        return metrics;
    }
    
    @Override
    public Object getTransactionMetrics() {
        List<OrderDTO> orders = orderServiceClient.getAllOrders();
        
        long successfulTransactions = orders.stream()
                .filter(order -> "DELIVERED".equalsIgnoreCase(order.getStatus()) || 
                                "SHIPPED".equalsIgnoreCase(order.getStatus()))
                .count();
        
        long failedTransactions = orders.size() - successfulTransactions;
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalTransactions", orders.size());
        metrics.put("successfulTransactions", successfulTransactions);
        metrics.put("failedTransactions", failedTransactions);
        metrics.put("successRate", orders.isEmpty() ? 0.0 : (double) successfulTransactions / orders.size() * 100);
        
        return metrics;
    }
}