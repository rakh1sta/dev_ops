package com.agricultural.productservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.agricultural.productservice.repository")
public class ProductServiceConfig {
    // Configuration class for Product Service
}