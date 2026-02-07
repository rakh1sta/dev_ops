package com.agricultural.notification.handler;

import com.agricultural.notification.dto.OrderEvent;
import com.agricultural.notification.service.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderNotificationHandler {
    
    private final EmailSender emailSender;
    
    public void handleOrderEvent(OrderEvent orderEvent) {
        String subject = "Order Confirmation - #" + orderEvent.getOrderId();
        String body = createOrderConfirmationMessage(orderEvent);
        
        emailSender.sendEmail(orderEvent.getCustomerEmail(), subject, body);
    }
    
    public void handleOrderStatusUpdate(OrderEvent orderEvent) {
        String subject = "Order Status Update - #" + orderEvent.getOrderId();
        String body = createOrderStatusUpdateMessage(orderEvent);
        
        emailSender.sendEmail(orderEvent.getCustomerEmail(), subject, body);
    }
    
    private String createOrderConfirmationMessage(OrderEvent orderEvent) {
        return String.format(
            """
            Dear Customer,
            
            Your order #%d has been successfully placed.
            
            Order Details:
            - Total Amount: $%.2f
            - Status: %s
            
            Thank you for choosing our service!
            
            Best regards,
            Agricultural Digital Platform Team
            """,
            orderEvent.getOrderId(),
            orderEvent.getTotalAmount(),
            orderEvent.getStatus()
        );
    }
    
    private String createOrderStatusUpdateMessage(OrderEvent orderEvent) {
        return String.format(
            """
            Dear Customer,
            
            Your order #%d status has been updated.
            
            New Status: %s
            - Total Amount: $%.2f
            
            Thank you for choosing our service!
            
            Best regards,
            Agricultural Digital Platform Team
            """,
            orderEvent.getOrderId(),
            orderEvent.getStatus(),
            orderEvent.getTotalAmount()
        );
    }
}