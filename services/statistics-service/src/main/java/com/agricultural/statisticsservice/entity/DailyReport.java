package com.agricultural.statisticsservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "report_date", unique = true, nullable = false)
    private LocalDate reportDate;
    
    @Column(name = "total_orders")
    private Long totalOrders;
    
    @Column(name = "total_revenue", precision = 10, scale = 2)
    private BigDecimal totalRevenue;
    
    @Column(name = "active_users")
    private Long activeUsers;
    
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}