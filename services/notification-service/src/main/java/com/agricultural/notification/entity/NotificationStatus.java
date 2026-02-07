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
public class NotificationStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "notification_id", nullable = false)
    private String notificationId;
    
    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatusEnum status;
    
    @Column(name = "subject", length = 500)
    private String subject;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "channel") // EMAIL, SMS, PUSH
    private String channel;
    
    @Column(name = "retry_count", defaultValue = "0")
    private Integer retryCount;
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum NotificationStatusEnum {
        PENDING,
        SENT,
        FAILED,
        RETRYING
    }
}