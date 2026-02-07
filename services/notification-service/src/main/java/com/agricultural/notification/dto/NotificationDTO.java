package com.agricultural.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private String id;

    private String type;

    private String title;
    private String message;

    private String recipientId;

    private String senderId;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private String priority; // LOW, MEDIUM, HIGH
    private String category; // SYSTEM, ALERT, INFO, etc.
}