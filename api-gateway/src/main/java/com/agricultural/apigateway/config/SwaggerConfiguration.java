package com.agricultural.apigateway.config;

import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashSet;
import java.util.Set;
@Configuration
public class SwaggerConfiguration {

    @Bean
    @Primary
    public SwaggerUiConfigProperties swaggerUiConfigProperties(SwaggerUiConfigProperties configProps) {
        Set<SwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();

        urls.add(new SwaggerUiConfigProperties.SwaggerUrl("users-service", "/users-service/v3/api-docs", "Users API"));
        urls.add(new SwaggerUiConfigProperties.SwaggerUrl("product-service", "/product-service/v3/api-docs", "Product API"));
        urls.add(new SwaggerUiConfigProperties.SwaggerUrl("order-service", "/order-service/v3/api-docs", "Order API"));
        urls.add(new SwaggerUiConfigProperties.SwaggerUrl("notification-service", "/notification-service/v3/api-docs", "Notification API"));
        urls.add(new SwaggerUiConfigProperties.SwaggerUrl("statistics-service", "/statistics-service/v3/api-docs", "Statistics API"));

        configProps.setUrls(urls);
        return configProps;
    }
}