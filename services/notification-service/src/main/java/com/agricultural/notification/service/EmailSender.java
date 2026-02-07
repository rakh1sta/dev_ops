package com.agricultural.notification.service;

public interface EmailSender {
    void sendEmail(String to, String subject, String body);
}