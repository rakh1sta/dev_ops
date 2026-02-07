package com.agricultural.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
    
    @Column(name = "total_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPrice;
    
    @PrePersist
    protected void calculateTotalPrice() {
        if (quantity != null && price != null) {
            totalPrice = price.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    @PreUpdate
    protected void recalculateTotalPrice() {
        if (quantity != null && price != null) {
            totalPrice = price.multiply(BigDecimal.valueOf(quantity));
        }
    }
}