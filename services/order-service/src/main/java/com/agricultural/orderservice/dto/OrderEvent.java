package com.agricultural.orderservice.dto;

import com.agricultural.orderservice.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    
    private Long orderId;
    private Long customerId;
    private String customerEmail;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    
    public static OrderEvent fromOrderAndCustomerEmail(com.agricultural.orderservice.entity.Order order, String customerEmail) {
        OrderEvent event = new OrderEvent();
        event.setOrderId(order.getId());
        event.setCustomerId(order.getCustomerId());
        event.setCustomerEmail(customerEmail);
        event.setTotalAmount(order.getTotalAmount());
        event.setStatus(order.getStatus());
        return event;
    }
}