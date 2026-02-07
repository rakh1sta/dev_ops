//package com.agricultural.notification.service.impl;
//
//import com.agricultural.notification.dto.NotificationDTO;
//import com.agricultural.notification.entity.Notification;
//import com.agricultural.notification.entity.NotificationPriority;
//import com.agricultural.notification.entity.NotificationStatus;
//import com.agricultural.notification.entity.NotificationType;
//import com.agricultural.notification.repository.NotificationStatusRepository;
//import com.agricultural.notification.service.NotificationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.context.Context;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class NotificationServiceImpl implements NotificationService {
//
//    private final JavaMailSender mailSender;
//    private final TemplateEngine templateEngine;
//    private final NotificationStatusRepository notificationStatusRepository;
//    private final RabbitTemplate rabbitTemplate;
//
//    @Value("${spring.rabbitmq.template.exchange:notification.main.exchange}")
//    private String exchangeName;
//
//    @Value("${spring.rabbitmq.template.routing-key:notification.main.routing.key}")
//    private String routingKey;
//
//    @Value("${spring.rabbitmq.template.retry-routing-key:notification.retry.routing.key}")
//    private String retryRoutingKey;
//
//    @Value("${notification.max.retry.attempts:3}")
//    private int maxRetryAttempts;
//
//    @Override
//    public String sendNotification(NotificationDTO notificationDTO) {
//        try {
//            // Convert DTO to entity
//            Notification notification = createNotificationFromDTO(notificationDTO);
//            notification = saveNotification(notification);
//
//            // Process the notification based on type
//            switch (notification.getType()) {
//                case EMAIL:
//                    sendEmail(notification.getRecipientId(), notification.getTitle(), notification.getMessage());
//                    break;
//                case SMS:
//                    sendSms(notification.getRecipientId(), notification.getMessage());
//                    break;
//                case PUSH:
//                    sendPushNotification(notification.getRecipientId(), notification.getTitle(), notification.getMessage());
//                    break;
//                default:
//                    throw new IllegalArgumentException("Unsupported notification type: " + notification.getType());
//            }
//
//            // Update notification status
//            notification.setStatus(com.agricultural.notification.entity.NotificationStatus.SENT);
//            notification.setSentAt(LocalDateTime.now());
//            saveNotification(notification);
//
//            // Save successful notification status
//            NotificationStatus status = new NotificationStatus();
//            status.setNotificationId(notification.getExternalId());
//            status.setRecipientEmail(notification.getRecipientId());
//            status.setSenderId(notification.getSenderId());
//            status.setStatus(com.agricultural.notification.entity.NotificationStatus.SENT);
//            status.setSubject(notification.getTitle());
//            status.setContent(notification.getMessage());
//            status.setChannel(notification.getType().toString());
//            status.setPriority(notification.getPriority() != null ? notification.getPriority().toString() : null);
//            status.setCategory(notification.getCategory() != null ? notification.getCategory().toString() : null);
//            status.setCreatedAt(LocalDateTime.now());
//            status.setSentAt(LocalDateTime.now());
//            status.setRetryCount(0);
//
//            notificationStatusRepository.save(status);
//
//            log.info("Notification sent successfully with ID: {}", notification.getExternalId());
//            return notification.getExternalId();
//
//        } catch (Exception e) {
//            log.error("Failed to send notification to {}: {}",
//                      notificationDTO.getRecipientId(), e.getMessage());
//
//            // Find the saved notification to update its status
//            Notification notification = createNotificationFromDTO(notificationDTO);
//            notification.setStatus(com.agricultural.notification.entity.NotificationStatus.FAILED);
//            saveNotification(notification);
//
//            // Save failed notification status
//            NotificationStatus status = new NotificationStatus();
//            status.setNotificationId(notification.getExternalId());
//            status.setRecipientEmail(notificationDTO.getRecipientId());
//            status.setSenderId(notificationDTO.getSenderId());
//            status.setStatus(com.agricultural.notification.entity.NotificationStatus.FAILED);
//            status.setSubject(notificationDTO.getTitle());
//            status.setContent(notificationDTO.getMessage());
//            status.setChannel(notificationDTO.getType().toUpperCase());
//            status.setPriority(notificationDTO.getPriority());
//            status.setCategory(notificationDTO.getCategory());
//            status.setErrorMessage(e.getMessage());
//            status.setCreatedAt(LocalDateTime.now());
//            status.setRetryCount(1);
//
//            notificationStatusRepository.save(status);
//
//            // Send to retry queue
//            sendToRetryQueue(status);
//
//            return notification.getExternalId();
//        }
//    }
//
//    @Override
//    public Notification createNotificationFromDTO(NotificationDTO notificationDTO) {
//        return Notification.builder()
//                .externalId(notificationDTO.getId() != null ? notificationDTO.getId() : UUID.randomUUID().toString())
//                .type(NotificationType.valueOf(notificationDTO.getType().toUpperCase()))
//                .title(notificationDTO.getTitle())
//                .message(notificationDTO.getMessage())
//                .recipientId(notificationDTO.getRecipientId())
//                .senderId(notificationDTO.getSenderId())
//                .priority(notificationDTO.getPriority() != null ?
//                         NotificationPriority.valueOf(notificationDTO.getPriority().toUpperCase()) : null)
//                .category(notificationDTO.getCategory() != null ?
//                         com.agricultural.notification.entity.NotificationCategory.valueOf(
//                             notificationDTO.getCategory().toUpperCase()) : null)
//                .isRead(notificationDTO.isRead())
//                .createdAt(notificationDTO.getCreatedAt() != null ?
//                          notificationDTO.getCreatedAt() : LocalDateTime.now())
//                .sentAt(notificationDTO.getSentAt())
//                .status(com.agricultural.notification.entity.NotificationStatus.PENDING)
//                .build();
//    }
//
//    @Override
//    public NotificationDTO convertToDTO(Notification notification) {
//        return NotificationDTO.builder()
//                .id(notification.getExternalId())
//                .type(notification.getType() != null ? notification.getType().toString() : null)
//                .title(notification.getTitle())
//                .message(notification.getMessage())
//                .recipientId(notification.getRecipientId())
//                .senderId(notification.getSenderId())
//                .priority(notification.getPriority() != null ? notification.getPriority().toString() : null)
//                .category(notification.getCategory() != null ? notification.getCategory().toString() : null)
//                .isRead(notification.getIsRead() != null ? notification.getIsRead() : false)
//                .createdAt(notification.getCreatedAt())
//                .sentAt(notification.getSentAt())
//                .build();
//    }
//
//    @Override
//    public void sendPriceChangeNotification(String recipient, String productName, String oldPrice, String newPrice, String changePercentage) {
//        try {
//            // Prepare Thymeleaf context
//            Context context = new Context();
//            context.setVariable("userName", recipient);
//            context.setVariable("productName", productName);
//            context.setVariable("oldPrice", oldPrice);
//            context.setVariable("newPrice", newPrice);
//            context.setVariable("changePercentage", changePercentage);
//            context.setVariable("changeDate", LocalDateTime.now().toString());
//
//            String htmlContent = templateEngine.process("price-change-alert", context);
//
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//            helper.setTo(recipient);
//            helper.setSubject("Price Change Alert for " + productName);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(mimeMessage);
//
//            // Save successful notification status
//            String notificationId = UUID.randomUUID().toString();
//            NotificationStatus status = new NotificationStatus();
//            status.setNotificationId(notificationId);
//            status.setRecipientEmail(recipient);
//            status.setStatus(com.agricultural.notification.entity.NotificationStatus.SENT);
//            status.setSubject("Price Change Alert for " + productName);
//            status.setContent(htmlContent);
//            status.setChannel("EMAIL");
//            status.setCreatedAt(LocalDateTime.now());
//            status.setRetryCount(0);
//
//            notificationStatusRepository.save(status);
//
//        } catch (MessagingException e) {
//            log.error("Failed to send price change notification to {}: {}", recipient, e.getMessage());
//
//            String notificationId = UUID.randomUUID().toString();
//
//            // Save failed notification status
//            NotificationStatus status = new NotificationStatus();
//            status.setNotificationId(notificationId);
//            status.setRecipientEmail(recipient);
//            status.setStatus(com.agricultural.notification.entity.NotificationStatus.FAILED);
//            status.setSubject("Price Change Alert for " + productName);
//            status.setChannel("EMAIL");
//            status.setErrorMessage(e.getMessage());
//            status.setCreatedAt(LocalDateTime.now());
//            status.setRetryCount(1);
//
//            notificationStatusRepository.save(status);
//
//            // Send to retry queue
//            sendToRetryQueue(status);
//        }
//    }
//
//    @Override
//    public void sendOrderNotification(String recipient, String orderNumber, String status, String message) {
//        try {
//            // Prepare Thymeleaf context
//            Context context = new Context();
//            context.setVariable("customerName", recipient);
//            context.setVariable("orderNumber", orderNumber);
//            context.setVariable("orderStatus", status);
//            context.setVariable("message", message);
//            context.setVariable("orderDate", LocalDateTime.now());
//
//            String htmlContent = templateEngine.process("order-notification", context);
//
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//            helper.setTo(recipient);
//            helper.setSubject("Order Update - Order #" + orderNumber);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(mimeMessage);
//
//            // Save successful notification status
//            String notificationId = UUID.randomUUID().toString();
//            NotificationStatus notificationStatus = new NotificationStatus();
//            notificationStatus.setNotificationId(notificationId);
//            notificationStatus.setRecipientEmail(recipient);
//            notificationStatus.setStatus(com.agricultural.notification.entity.NotificationStatus.SENT);
//            notificationStatus.setSubject("Order Update - Order #" + orderNumber);
//            notificationStatus.setContent(htmlContent);
//            notificationStatus.setChannel("EMAIL");
//            notificationStatus.setCreatedAt(LocalDateTime.now());
//            notificationStatus.setRetryCount(0);
//
//            notificationStatusRepository.save(notificationStatus);
//
//        } catch (MessagingException e) {
//            log.error("Failed to send order notification to {}: {}", recipient, e.getMessage());
//
//            String notificationId = UUID.randomUUID().toString();
//
//            // Save failed notification status
//            NotificationStatus notificationStatus = new NotificationStatus();
//            notificationStatus.setNotificationId(notificationId);
//            notificationStatus.setRecipientEmail(recipient);
//            notificationStatus.setStatus(com.agricultural.notification.entity.NotificationStatus.FAILED);
//            notificationStatus.setSubject("Order Update - Order #" + orderNumber);
//            notificationStatus.setChannel("EMAIL");
//            notificationStatus.setErrorMessage(e.getMessage());
//            notificationStatus.setCreatedAt(LocalDateTime.now());
//            notificationStatus.setRetryCount(1);
//
//            notificationStatusRepository.save(notificationStatus);
//
//            // Send to retry queue
//            sendToRetryQueue(notificationStatus);
//        }
//    }
//
//    @Override
//    public void saveNotificationStatus(NotificationStatus notificationStatus) {
//        notificationStatusRepository.save(notificationStatus);
//    }
//
//    @Override
//    public void handleNotificationFailure(String notificationId, String errorMessage) {
//        // Find the notification status and increment retry count
//        var statuses = notificationStatusRepository.findByNotificationId(notificationId);
//        if (!statuses.isEmpty()) {
//            NotificationStatus status = statuses.get(0);
//            int newRetryCount = status.getRetryCount() + 1;
//
//            if (newRetryCount <= maxRetryAttempts) {
//                status.setStatus(com.agricultural.notification.entity.NotificationStatus.RETRYING);
//                status.setRetryCount(newRetryCount);
//                status.setErrorMessage(errorMessage);
//                status.setUpdatedAt(LocalDateTime.now());
//
//                notificationStatusRepository.save(status);
//
//                // Send to retry queue
//                sendToRetryQueue(status);
//            } else {
//                status.setStatus(com.agricultural.notification.entity.NotificationStatus.FAILED);
//                status.setUpdatedAt(LocalDateTime.now());
//                notificationStatusRepository.save(status);
//                log.error("Max retry attempts reached for notification: {}", notificationId);
//            }
//        }
//    }
//
//    @Override
//    public void retryFailedNotifications() {
//        var failedNotifications = notificationStatusRepository.findByStatusAndRetryCountLessThan(
//                com.agricultural.notification.entity.NotificationStatus.FAILED, maxRetryAttempts);
//
//        for (NotificationStatus status : failedNotifications) {
//            log.info("Retrying notification: {}", status.getNotificationId());
//
//            status.setStatus(com.agricultural.notification.entity.NotificationStatus.RETRYING);
//            status.setRetryCount(status.getRetryCount() + 1);
//            status.setUpdatedAt(LocalDateTime.now());
//
//            notificationStatusRepository.save(status);
//
//            // Send to retry queue
//            sendToRetryQueue(status);
//        }
//    }
//
//    @Override
//    public void markAsRead(String notificationId) {
//        // Update notification status to mark as read
//        var statuses = notificationStatusRepository.findByNotificationId(notificationId);
//        if (!statuses.isEmpty()) {
//            NotificationStatus status = statuses.get(0);
//            status.setRead(true);
//            status.setReadAt(LocalDateTime.now());
//            notificationStatusRepository.save(status);
//            log.info("Marked notification as read: {}", notificationId);
//        }
//
//        // Also update the notification entity
//        // Note: In a real implementation, you would have a NotificationRepository to update the entity
//        // For now, we'll just log that this should happen
//        log.info("Notification marked as read in status table: {}", notificationId);
//    }
//
//    @Override
//    public NotificationDTO getNotificationById(String id) {
//        var statuses = notificationStatusRepository.findByNotificationId(id);
//        if (!statuses.isEmpty()) {
//            NotificationStatus status = statuses.get(0);
//            return NotificationDTO.builder()
//                    .id(status.getNotificationId())
//                    .type(status.getChannel())
//                    .title(status.getSubject())
//                    .message(status.getContent())
//                    .recipientId(status.getRecipientEmail())
//                    .senderId(status.getSenderId())
//                    .isRead(status.isRead())
//                    .createdAt(status.getCreatedAt())
//                    .sentAt(status.getSentAt())
//                    .priority(status.getPriority())
//                    .category(status.getCategory())
//                    .build();
//        }
//        return null;
//    }
//
//    @Override
//    public List<NotificationDTO> getNotificationsByRecipient(String recipientId) {
//        List<NotificationStatus> statuses = notificationStatusRepository.findByRecipientEmail(recipientId);
//        return statuses.stream()
//                .map(status -> NotificationDTO.builder()
//                        .id(status.getNotificationId())
//                        .type(status.getChannel())
//                        .title(status.getSubject())
//                        .message(status.getContent())
//                        .recipientId(status.getRecipientEmail())
//                        .senderId(status.getSenderId())
//                        .isRead(status.isRead())
//                        .createdAt(status.getCreatedAt())
//                        .sentAt(status.getSentAt())
//                        .priority(status.getPriority())
//                        .category(status.getCategory())
//                        .build())
//                .toList();
//    }
//
//    @Override
//    public List<NotificationDTO> getUnreadNotificationsByRecipient(String recipientId) {
//        List<NotificationStatus> statuses = notificationStatusRepository.findByRecipientEmailAndIsReadFalse(recipientId);
//        return statuses.stream()
//                .map(status -> NotificationDTO.builder()
//                        .id(status.getNotificationId())
//                        .type(status.getChannel())
//                        .title(status.getSubject())
//                        .message(status.getContent())
//                        .recipientId(status.getRecipientEmail())
//                        .senderId(status.getSenderId())
//                        .isRead(status.isRead())
//                        .createdAt(status.getCreatedAt())
//                        .sentAt(status.getSentAt())
//                        .priority(status.getPriority())
//                        .category(status.getCategory())
//                        .build())
//                .toList();
//    }
//
//    @Override
//    public Notification saveNotification(Notification notification) {
//        // In a real implementation, you would have a NotificationRepository here
//        // For now, we'll just return the notification as-is
//        // This method is a placeholder for when the NotificationRepository is added
//        log.info("Saving notification: {}", notification.getExternalId());
//        return notification;
//    }
//
//    @RabbitListener(queues = "notification.retry.queue")
//    public void handleRetryNotification(NotificationStatus notificationStatus) {
//        try {
//            // Attempt to resend the notification based on its type
//            if (notificationStatus.getChannel().equals("EMAIL")) {
//                // Resend email
//                MimeMessage mimeMessage = mailSender.createMimeMessage();
//                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//                helper.setTo(notificationStatus.getRecipientEmail());
//                helper.setSubject(notificationStatus.getSubject());
//                helper.setText(notificationStatus.getContent(), true);
//
//                mailSender.send(mimeMessage);
//
//                // Update status to SENT
//                notificationStatus.setStatus(com.agricultural.notification.entity.NotificationStatus.SENT);
//                notificationStatus.setErrorMessage(null);
//                notificationStatus.setUpdatedAt(LocalDateTime.now());
//                notificationStatusRepository.save(notificationStatus);
//
//                log.info("Successfully resent notification: {}", notificationStatus.getNotificationId());
//            } else if (notificationStatus.getChannel().equals("SMS")) {
//                // Resend SMS
//                sendSms(notificationStatus.getRecipientEmail(), notificationStatus.getContent());
//
//                // Update status to SENT
//                notificationStatus.setStatus(com.agricultural.notification.entity.NotificationStatus.SENT);
//                notificationStatus.setErrorMessage(null);
//                notificationStatus.setUpdatedAt(LocalDateTime.now());
//                notificationStatusRepository.save(notificationStatus);
//
//                log.info("Successfully resent SMS notification: {}", notificationStatus.getNotificationId());
//            }
//        } catch (Exception e) {
//            log.error("Failed to retry notification: {} - Error: {}",
//                     notificationStatus.getNotificationId(), e.getMessage());
//
//            int newRetryCount = notificationStatus.getRetryCount() + 1;
//            if (newRetryCount >= maxRetryAttempts) {
//                notificationStatus.setStatus(com.agricultural.notification.entity.NotificationStatus.FAILED);
//            } else {
//                notificationStatus.setStatus(com.agricultural.notification.entity.NotificationStatus.RETRYING);
//                notificationStatus.setRetryCount(newRetryCount);
//                notificationStatus.setErrorMessage(e.getMessage());
//                notificationStatus.setUpdatedAt(LocalDateTime.now());
//
//                // Send back to retry queue with delay
//                rabbitTemplate.convertAndSend(exchangeName, retryRoutingKey, notificationStatus);
//            }
//
//            notificationStatusRepository.save(notificationStatus);
//        }
//    }
//
//    private void sendEmail(String to, String subject, String content) throws MessagingException {
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(content, true);
//
//        mailSender.send(mimeMessage);
//    }
//
//    private void sendSms(String phoneNumber, String message) {
//        // Implement SMS sending logic
//        log.info("Sending SMS to: {} with message: {}", phoneNumber, message);
//        // In a real implementation, you would integrate with an SMS service like Twilio
//    }
//
//    private void sendPushNotification(String userId, String title, String body) {
//        // Implement push notification logic
//        log.info("Sending push notification to user: {} with title: {} and body: {}", userId, title, body);
//        // In a real implementation, you would integrate with Firebase Cloud Messaging or similar
//    }
//
//    private void sendToRetryQueue(NotificationStatus notificationStatus) {
//        try {
//            rabbitTemplate.convertAndSend(exchangeName, retryRoutingKey, notificationStatus);
//            log.info("Sent notification to retry queue: {}", notificationStatus.getNotificationId());
//        } catch (Exception e) {
//            log.error("Failed to send notification to retry queue: {}", e.getMessage());
//        }
//    }
//}