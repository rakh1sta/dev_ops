package com.agricultural.statisticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyReportDTO {
    
    private Long id;
    private LocalDate reportDate;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Long activeUsers;
    private java.time.LocalDateTime createdAt;
}