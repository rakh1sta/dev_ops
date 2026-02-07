package com.agricultural.notification.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationWebSocketService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public void sendPriceAlertNotification(Object alertData) {
        messagingTemplate.convertAndSend("/topic/price-alerts", alertData);
    }
    
    public void sendOrderNotification(Object orderData) {
        messagingTemplate.convertAndSend("/topic/order-notifications", orderData);
    }
    
    public void sendGeneralNotification(Object notificationData) {
        messagingTemplate.convertAndSend("/topic/general-notifications", notificationData);
    }
}