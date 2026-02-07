package com.agricultural.notification.service;

import com.agricultural.notification.entity.NotificationStatus;

public interface NotificationService {
    
    void sendNotification(String recipient, String subject, String content, String channel);
    
    void sendPriceChangeNotification(String recipient, String productName, String oldPrice, String newPrice, String changePercentage);
    
    void sendOrderNotification(String recipient, String orderNumber, String status, String message);
    
    void saveNotificationStatus(NotificationStatus notificationStatus);
    
    void handleNotificationFailure(String notificationId, String errorMessage);
    
    void retryFailedNotifications();
}