package com.agricultural.orderservice.dto;

import com.agricultural.orderservice.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    
    private Long id;
    private Long orderId;
    private Transaction.PaymentStatus paymentStatus;
    private LocalDateTime transactionDate;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}