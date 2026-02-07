package com.agricultural.notification.service.impl;

import com.agricultural.notification.entity.NotificationStatus;
import com.agricultural.notification.entity.NotificationStatus.NotificationStatusEnum;
import com.agricultural.notification.repository.NotificationStatusRepository;
import com.agricultural.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final NotificationStatusRepository notificationStatusRepository;
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${spring.rabbitmq.template.exchange:notification.main.exchange}")
    private String exchangeName;
    
    @Value("${spring.rabbitmq.template.routing-key:notification.main.routing.key}")
    private String routingKey;
    
    @Value("${spring.rabbitmq.template.retry-routing-key:notification.retry.routing.key}")
    private String retryRoutingKey;
    
    @Value("${notification.max.retry.attempts:3}")
    private int maxRetryAttempts;
    
    @Override
    public void sendNotification(String recipient, String subject, String content, String channel) {
        try {
            if ("EMAIL".equalsIgnoreCase(channel)) {
                sendEmail(recipient, subject, content);
            } else if ("SMS".equalsIgnoreCase(channel)) {
                // Implement SMS sending logic
                log.info("Sending SMS to: {} with subject: {}", recipient, subject);
            } else if ("PUSH".equalsIgnoreCase(channel)) {
                // Implement push notification logic
                log.info("Sending push notification to: {}", recipient);
            }
            
            // Save successful notification status
            NotificationStatus status = new NotificationStatus();
            status.setNotificationId(generateNotificationId());
            status.setRecipientEmail(recipient);
            status.setStatus(NotificationStatusEnum.SENT);
            status.setSubject(subject);
            status.setContent(content);
            status.setChannel(channel);
            status.setRetryCount(0);
            
            notificationStatusRepository.save(status);
            
        } catch (Exception e) {
            log.error("Failed to send notification to {}: {}", recipient, e.getMessage());
            
            // Save failed notification status
            NotificationStatus status = new NotificationStatus();
            status.setNotificationId(generateNotificationId());
            status.setRecipientEmail(recipient);
            status.setStatus(NotificationStatusEnum.FAILED);
            status.setSubject(subject);
            status.setContent(content);
            status.setChannel(channel);
            status.setErrorMessage(e.getMessage());
            status.setRetryCount(1);
            
            notificationStatusRepository.save(status);
            
            // Send to retry queue
            sendToRetryQueue(status);
        }
    }
    
    @Override
    public void sendPriceChangeNotification(String recipient, String productName, String oldPrice, String newPrice, String changePercentage) {
        try {
            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("userName", recipient);
            context.setVariable("productName", productName);
            context.setVariable("oldPrice", oldPrice);
            context.setVariable("newPrice", newPrice);
            context.setVariable("changePercentage", changePercentage);
            context.setVariable("changeDate", java.time.LocalDateTime.now().toString());
            
            String htmlContent = templateEngine.process("price-change-alert", context);
            
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(recipient);
            helper.setSubject("Price Change Alert for " + productName);
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            
            // Save successful notification status
            NotificationStatus status = new NotificationStatus();
            status.setNotificationId(generateNotificationId());
            status.setRecipientEmail(recipient);
            status.setStatus(NotificationStatusEnum.SENT);
            status.setSubject("Price Change Alert for " + productName);
            status.setContent(htmlContent);
            status.setChannel("EMAIL");
            status.setRetryCount(0);
            
            notificationStatusRepository.save(status);
            
        } catch (MessagingException e) {
            log.error("Failed to send price change notification to {}: {}", recipient, e.getMessage());
            
            // Save failed notification status
            NotificationStatus status = new NotificationStatus();
            status.setNotificationId(generateNotificationId());
            status.setRecipientEmail(recipient);
            status.setStatus(NotificationStatusEnum.FAILED);
            status.setSubject("Price Change Alert for " + productName);
            status.setChannel("EMAIL");
            status.setErrorMessage(e.getMessage());
            status.setRetryCount(1);
            
            notificationStatusRepository.save(status);
            
            // Send to retry queue
            sendToRetryQueue(status);
        }
    }
    
    @Override
    public void sendOrderNotification(String recipient, String orderNumber, String status, String message) {
        try {
            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("customerName", recipient);
            context.setVariable("orderNumber", orderNumber);
            context.setVariable("orderStatus", status);
            context.setVariable("message", message);
            context.setVariable("orderDate", java.time.LocalDateTime.now());
            
            String htmlContent = templateEngine.process("order-notification", context);
            
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(recipient);
            helper.setSubject("Order Update - Order #" + orderNumber);
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            
            // Save successful notification status
            NotificationStatus notificationStatus = new NotificationStatus();
            notificationStatus.setNotificationId(generateNotificationId());
            notificationStatus.setRecipientEmail(recipient);
            notificationStatus.setStatus(NotificationStatusEnum.SENT);
            notificationStatus.setSubject("Order Update - Order #" + orderNumber);
            notificationStatus.setContent(htmlContent);
            notificationStatus.setChannel("EMAIL");
            notificationStatus.setRetryCount(0);
            
            notificationStatusRepository.save(notificationStatus);
            
        } catch (MessagingException e) {
            log.error("Failed to send order notification to {}: {}", recipient, e.getMessage());
            
            // Save failed notification status
            NotificationStatus notificationStatus = new NotificationStatus();
            notificationStatus.setNotificationId(generateNotificationId());
            notificationStatus.setRecipientEmail(recipient);
            notificationStatus.setStatus(NotificationStatusEnum.FAILED);
            notificationStatus.setSubject("Order Update - Order #" + orderNumber);
            notificationStatus.setChannel("EMAIL");
            notificationStatus.setErrorMessage(e.getMessage());
            notificationStatus.setRetryCount(1);
            
            notificationStatusRepository.save(notificationStatus);
            
            // Send to retry queue
            sendToRetryQueue(notificationStatus);
        }
    }
    
    @Override
    public void saveNotificationStatus(NotificationStatus notificationStatus) {
        notificationStatusRepository.save(notificationStatus);
    }
    
    @Override
    public void handleNotificationFailure(String notificationId, String errorMessage) {
        // Find the notification status and increment retry count
        var statuses = notificationStatusRepository.findByNotificationId(notificationId);
        if (!statuses.isEmpty()) {
            NotificationStatus status = statuses.get(0);
            int newRetryCount = status.getRetryCount() + 1;
            
            if (newRetryCount <= maxRetryAttempts) {
                status.setStatus(NotificationStatusEnum.RETRYING);
                status.setRetryCount(newRetryCount);
                status.setErrorMessage(errorMessage);
                
                notificationStatusRepository.save(status);
                
                // Send to retry queue
                sendToRetryQueue(status);
            } else {
                status.setStatus(NotificationStatusEnum.FAILED);
                notificationStatusRepository.save(status);
                log.error("Max retry attempts reached for notification: {}", notificationId);
            }
        }
    }
    
    @Override
    public void retryFailedNotifications() {
        var failedNotifications = notificationStatusRepository.findByStatusAndRetryCountLessThan(
                NotificationStatusEnum.FAILED, maxRetryAttempts);
        
        for (NotificationStatus status : failedNotifications) {
            log.info("Retrying notification: {}", status.getNotificationId());
            
            status.setStatus(NotificationStatusEnum.RETRYING);
            status.setRetryCount(status.getRetryCount() + 1);
            
            notificationStatusRepository.save(status);
            
            // Send to retry queue
            sendToRetryQueue(status);
        }
    }
    
    @RabbitListener(queues = "notification.retry.queue")
    public void handleRetryNotification(NotificationStatus notificationStatus) {
        try {
            // Attempt to resend the notification based on its type
            if (notificationStatus.getChannel().equals("EMAIL")) {
                // Resend email
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setTo(notificationStatus.getRecipientEmail());
                helper.setSubject(notificationStatus.getSubject());
                helper.setText(notificationStatus.getContent(), true);
                
                mailSender.send(mimeMessage);
                
                // Update status to SENT
                notificationStatus.setStatus(NotificationStatusEnum.SENT);
                notificationStatus.setErrorMessage(null);
                notificationStatusRepository.save(notificationStatus);
                
                log.info("Successfully resent notification: {}", notificationStatus.getNotificationId());
            }
        } catch (Exception e) {
            log.error("Failed to retry notification: {} - Error: {}", 
                     notificationStatus.getNotificationId(), e.getMessage());
            
            int newRetryCount = notificationStatus.getRetryCount() + 1;
            if (newRetryCount >= maxRetryAttempts) {
                notificationStatus.setStatus(NotificationStatusEnum.FAILED);
            } else {
                notificationStatus.setStatus(NotificationStatusEnum.RETRYING);
                notificationStatus.setRetryCount(newRetryCount);
                notificationStatus.setErrorMessage(e.getMessage());
                
                // Send back to retry queue with delay
                rabbitTemplate.convertAndSend(exchangeName, retryRoutingKey, notificationStatus);
            }
            
            notificationStatusRepository.save(notificationStatus);
        }
    }
    
    private void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        
        mailSender.send(mimeMessage);
    }
    
    private String generateNotificationId() {
        return "NOTIF_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
    
    private void sendToRetryQueue(NotificationStatus notificationStatus) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, retryRoutingKey, notificationStatus);
            log.info("Sent notification to retry queue: {}", notificationStatus.getNotificationId());
        } catch (Exception e) {
            log.error("Failed to send notification to retry queue: {}", e.getMessage());
        }
    }
}