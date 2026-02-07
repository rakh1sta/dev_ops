package com.agricultural.statisticsservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "current_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal currentPrice;

    @Column(name = "previous_price", precision = 10, scale = 2)
    private BigDecimal previousPrice;

    @Column(name = "price_change", precision = 10, scale = 2)
    private BigDecimal priceChange;

    @Column(name = "price_change_percentage", precision = 5, scale = 2)
    private BigDecimal priceChangePercentage;

    @Column(name = "market_id")
    private String marketId;

    @Column(name = "supplier_id")
    private String supplierId;

    @Column(name = "category")
    private String category;

    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    @Column(name = "region")
    private String region;

    @Column(name = "season")
    private String season;

    @Column(name = "harvest_year")
    private Integer harvestYear;

    @Column(name = "quality_grade")
    private String qualityGrade;

    @Column(name = "quantity_available")
    private BigDecimal quantityAvailable;

    @Column(name = "source_type")
    private String sourceType; // MANUAL, API, SCRAPED

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
