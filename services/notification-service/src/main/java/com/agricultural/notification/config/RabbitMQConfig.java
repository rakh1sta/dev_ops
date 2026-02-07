package com.agricultural.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    @Value("${rabbitmq.queue.notification.name:notification.queue}")
    private String notificationQueue;
    
    @Value("${rabbitmq.queue.notification.dlx.name:notification.dlx}")
    private String notificationDLQ;
    
    @Value("${rabbitmq.exchange.notification.name:notification.exchange}")
    private String notificationExchange;
    
    @Value("${rabbitmq.routing.key.notification.name:notification.routing.key}")
    private String notificationRoutingKey;
    
    @Value("${rabbitmq.retry.delay:10000}") // 10 seconds
    private int retryDelay;
    
    @Value("${rabbitmq.max.retry.attempts:3}")
    private int maxRetryAttempts;
    
    // Main notification queue with DLX configuration
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder
                .durable(notificationQueue)
                .withArgument("x-dead-letter-exchange", notificationDLQ)
                .withArgument("x-message-ttl", retryDelay) // TTL for retry
                .build();
    }
    
    // Dead letter queue for failed messages
    @Bean
    public Queue notificationDLQ() {
        return QueueBuilder
                .durable(notificationDLQ)
                .build();
    }
    
    // Main exchange
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(notificationExchange);
    }
    
    // Binding for main queue
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(notificationExchange())
                .with(notificationRoutingKey);
    }
    
    // Binding for dead letter queue (loops back to main exchange)
    @Bean
    public Binding notificationDLQBinding() {
        return BindingBuilder
                .bind(notificationDLQ())
                .to(notificationExchange())
                .with(notificationRoutingKey + ".retry");
    }
    
    // Retry exchange for retry mechanism
    @Bean
    public TopicExchange retryExchange() {
        return new TopicExchange("notification.retry.exchange");
    }
    
    // Retry queue with TTL and max attempts
    @Bean
    public Queue retryQueue() {
        return QueueBuilder
                .durable("notification.retry.queue")
                .withArgument("x-dead-letter-exchange", notificationDLQ) // After max retries, goes to DLQ
                .withArgument("x-message-ttl", retryDelay)
                .withArgument("x-max-attempts", maxRetryAttempts)
                .build();
    }
    
    @Bean
    public Binding retryBinding() {
        return BindingBuilder
                .bind(retryQueue())
                .to(retryExchange())
                .with("notification.retry");
    }
}