package com.agricultural.notification.service;

import com.agricultural.notification.dto.NotificationDTO;
import com.agricultural.notification.entity.Notification;
import com.agricultural.notification.entity.NotificationStatus;

public interface NotificationService {

    /**
     * Sends a notification based on the provided DTO
     */
    String sendNotification(NotificationDTO notificationDTO);

    /**
     * Creates a notification entity from DTO
     */
    Notification createNotificationFromDTO(NotificationDTO notificationDTO);

    /**
     * Converts a notification entity to DTO
     */
    NotificationDTO convertToDTO(Notification notification);

    /**
     * Sends a price change notification
     */
    void sendPriceChangeNotification(String recipient, String productName, String oldPrice, String newPrice, String changePercentage);

    /**
     * Sends an order notification
     */
    void sendOrderNotification(String recipient, String orderNumber, String status, String message);

    /**
     * Saves notification status to the database
     */
    void saveNotificationStatus(NotificationStatus notificationStatus);

    /**
     * Handles notification failure by attempting retries
     */
    void handleNotificationFailure(String notificationId, String errorMessage);

    /**
     * Retries all failed notifications
     */
    void retryFailedNotifications();

    /**
     * Marks a notification as read
     */
    void markAsRead(String notificationId);

    /**
     * Gets notification by ID
     */
    NotificationDTO getNotificationById(String id);

    /**
     * Gets all notifications for a specific recipient
     */
    java.util.List<NotificationDTO> getNotificationsByRecipient(String recipientId);

    /**
     * Gets all unread notifications for a specific recipient
     */
    java.util.List<NotificationDTO> getUnreadNotificationsByRecipient(String recipientId);
    
    /**
     * Saves a notification entity
     */
    Notification saveNotification(Notification notification);
}