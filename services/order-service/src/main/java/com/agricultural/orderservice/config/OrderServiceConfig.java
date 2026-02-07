package com.agricultural.orderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.agricultural.orderservice.repository")
public class OrderServiceConfig {
    // Configuration class for Order Service
}