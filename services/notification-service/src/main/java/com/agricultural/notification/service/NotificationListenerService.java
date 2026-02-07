//package com.agricultural.notification.service;
//
//import com.agricultural.notification.dto.NotificationDTO;
//import com.agricultural.notification.entity.NotificationStatus;
//import com.agricultural.notification.entity.NotificationStatus.NotificationStatusEnum;
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
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class NotificationListenerService {
//
//    private final JavaMailSender mailSender;
//    private final TemplateEngine templateEngine;
//    private final NotificationStatusRepository notificationStatusRepository;
//    private final RabbitTemplate rabbitTemplate;
//
//    @Value("${notification.max.retry.attempts:3}")
//    private int maxRetryAttempts;
//
//    @Value("${rabbitmq.exchange.retry:notification.retry.exchange}")
//    private String retryExchange;
//
//    @Value("${rabbitmq.routing-key.retry:notification.retry.routing.key}")
//    private String retryRoutingKey;
//
//    @RabbitListener(queues = "notification.main.queue")
//    public void handleNotification(NotificationDTO notificationDTO) {
//        try {
//            sendNotification(notificationDTO);
//
//            // Update status to SENT
//            updateNotificationStatus(notificationDTO.getId(), NotificationStatusEnum.SENT, null);
//
//        } catch (Exception e) {
//            log.error("Failed to send notification: {}", e.getMessage());
//
//            // Update status to FAILED and increment retry count
//            int currentRetryCount = getRetryCount(notificationDTO.getId());
//            if (currentRetryCount < maxRetryAttempts) {
//                updateNotificationStatus(notificationDTO.getId(), NotificationStatusEnum.RETRYING, e.getMessage());
//
//                // Send to retry queue with delay
//                rabbitTemplate.convertAndSend(retryExchange, retryRoutingKey, notificationDTO);
//            } else {
//                updateNotificationStatus(notificationDTO.getId(), NotificationStatusEnum.FAILED, e.getMessage());
//                log.error("Max retry attempts reached for notification: {}", notificationDTO.getId());
//            }
//        }
//    }
//
//    @RabbitListener(queues = "notification.retry.queue")
//    public void handleRetryNotification(NotificationDTO notificationDTO) {
//        try {
//            sendNotification(notificationDTO);
//
//            // Update status to SENT
//            updateNotificationStatus(notificationDTO.getId(), NotificationStatusEnum.SENT, null);
//
//            log.info("Successfully resent notification after retry: {}", notificationDTO.getId());
//
//        } catch (Exception e) {
//            log.error("Failed to send notification on retry: {}", e.getMessage());
//
//            int currentRetryCount = getRetryCount(notificationDTO.getId());
//            if (currentRetryCount < maxRetryAttempts) {
//                updateNotificationStatus(notificationDTO.getId(), NotificationStatusEnum.RETRYING, e.getMessage());
//
//                // Send back to retry queue with exponential backoff
//                rabbitTemplate.convertAndSend(retryExchange, retryRoutingKey, notificationDTO);
//            } else {
//                updateNotificationStatus(notificationDTO.getId(), NotificationStatusEnum.FAILED, e.getMessage());
//                log.error("Max retry attempts reached after retry for notification: {}", notificationDTO.getId());
//            }
//        }
//    }
//
//    private void sendNotification(NotificationDTO notificationDTO) throws MessagingException {
//        switch (notificationDTO.getType()) {
//            case "EMAIL":
//                sendEmailNotification(notificationDTO);
//                break;
//            case "SMS":
//                sendSmsNotification(notificationDTO);
//                break;
//            case "PUSH":
//                sendPushNotification(notificationDTO);
//                break;
//            default:
//                throw new IllegalArgumentException("Unsupported notification type: " + notificationDTO.getType());
//        }
//    }
//
//    private void sendEmailNotification(NotificationDTO notificationDTO) throws MessagingException {
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//
//        helper.setTo(notificationDTO.getRecipient());
//        helper.setSubject(notificationDTO.getSubject());
//
//        // Process Thymeleaf template if template name is provided
//        if (notificationDTO.getTemplateName() != null) {
//            Context context = new Context();
//            context.setVariables(notificationDTO.getTemplateVariables());
//
//            String htmlContent = templateEngine.process(notificationDTO.getTemplateName(), context);
//            helper.setText(htmlContent, true);
//        } else {
//            helper.setText(notificationDTO.getContent(), true);
//        }
//
//        mailSender.send(mimeMessage);
//    }
//
//    private void sendSmsNotification(NotificationDTO notificationDTO) {
//        // Implementation for SMS sending
//        log.info("Sending SMS to {}: {}", notificationDTO.getRecipient(), notificationDTO.getContent());
//    }
//
//    private void sendPushNotification(NotificationDTO notificationDTO) {
//        // Implementation for push notification sending
//        log.info("Sending push notification to: {}", notificationDTO.getRecipient());
//    }
//
//    private void updateNotificationStatus(String notificationId, NotificationStatusEnum status, String errorMessage) {
//        var optionalStatus = notificationStatusRepository.findByNotificationId(notificationId);
//        if (optionalStatus.isPresent()) {
//            NotificationStatus notificationStatus = optionalStatus.get();
//            notificationStatus.setStatus(status);
//            notificationStatus.setErrorMessage(errorMessage);
//            notificationStatus.setRetryCount(notificationStatus.getRetryCount() + 1);
//            notificationStatusRepository.save(notificationStatus);
//        } else {
//            // Create new status record if not exists
//            NotificationStatus newStatus = new NotificationStatus();
//            newStatus.setNotificationId(notificationId);
//            newStatus.setRecipientEmail(notificationDTO.getRecipient());
//            newStatus.setStatus(status);
//            newStatus.setErrorMessage(errorMessage);
//            newStatus.setRetryCount(1);
//            notificationStatusRepository.save(newStatus);
//        }
//    }
//
//    private int getRetryCount(String notificationId) {
//        var optionalStatus = notificationStatusRepository.findByNotificationId(notificationId);
//        return optionalStatus.map(NotificationStatus::getRetryCount).orElse(0);
//    }
//}