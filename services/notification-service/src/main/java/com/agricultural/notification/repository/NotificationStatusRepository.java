package com.agricultural.notification.repository;

import com.agricultural.notification.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationStatusRepository extends JpaRepository<NotificationStatus, Long> {
    
    List<NotificationStatus> findByNotificationId(String notificationId);
    
    List<NotificationStatus> findByRecipientEmail(String recipientEmail);
    
    List<NotificationStatus> findByStatus(NotificationStatus.NotificationStatusEnum status);
    
    List<NotificationStatus> findByStatusAndRetryCountLessThan(NotificationStatus.NotificationStatusEnum status, int maxRetries);
}