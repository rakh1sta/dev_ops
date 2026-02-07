package com.agricultural.notification.listener;

import com.agricultural.notification.dto.OrderEvent;
import com.agricultural.notification.handler.OrderNotificationHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {
    
    private final OrderNotificationHandler orderNotificationHandler;
    
    @RabbitListener(queues = "${spring.rabbitmq.template.order.queue:order.queue}")
    public void handleOrderEvent(OrderEvent orderEvent) {
        log.info("Received order event: {}", orderEvent);
        
        try {
            orderNotificationHandler.handleOrderEvent(orderEvent);
            log.info("Order notification processed successfully for order: {}", orderEvent.getOrderId());
        } catch (Exception e) {
            log.error("Error processing order event: {}", e.getMessage(), e);
        }
    }
    
    @RabbitListener(queues = "${spring.rabbitmq.template.order.queue:order.queue}")
    public void handleOrderStatusUpdate(OrderEvent orderEvent) {
        log.info("Received order status update event: {}", orderEvent);
        
        try {
            orderNotificationHandler.handleOrderStatusUpdate(orderEvent);
            log.info("Order status update notification processed successfully for order: {}", orderEvent.getOrderId());
        } catch (Exception e) {
            log.error("Error processing order status update event: {}", e.getMessage(), e);
        }
    }
}