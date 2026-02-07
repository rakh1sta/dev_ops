package com.agricultural.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "notification_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String externalId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private String title;
    private String message;
    private String recipientId;
    private String senderId;
    
    @Enumerated(EnumType.STRING)
    private NotificationPriority priority;
    
    @Enumerated(EnumType.STRING)
    private NotificationCategory category;
    
    private Boolean isRead = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;

}

