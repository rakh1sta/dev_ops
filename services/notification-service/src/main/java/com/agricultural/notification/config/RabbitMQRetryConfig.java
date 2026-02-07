package com.agricultural.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQRetryConfig {
    
    @Bean
    public Jackson2JsonMessageConverter producerJackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2JsonMessageConverter());
        return rabbitTemplate;
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(producerJackson2JsonMessageConverter());
        return factory;
    }
    
    // Retry queue with dead letter exchange configuration
    @Bean
    public Queue notificationRetryQueue() {
        return QueueBuilder
                .durable("notification.retry.queue")
                .withArgument("x-dead-letter-exchange", "notification.main.exchange") // Send back to main exchange after TTL
                .withArgument("x-message-ttl", 10000) // 10 seconds TTL for retry
                .withArgument("x-max-length", 1000) // Max 1000 messages in retry queue
                .build();
    }
    
    // Dead letter queue for failed notifications after max retries
    @Bean
    public Queue notificationDeadLetterQueue() {
        return QueueBuilder
                .durable("notification.dead.letter.queue")
                .build();
    }
    
    // Main exchange for notifications
    @Bean
    public DirectExchange notificationMainExchange() {
        return new DirectExchange("notification.main.exchange");
    }
    
    // Dead letter exchange
    @Bean
    public DirectExchange notificationDeadLetterExchange() {
        return new DirectExchange("notification.dead.letter.exchange");
    }
    
    // Binding for main queue
    @Bean
    public Binding notificationMainBinding() {
        return BindingBuilder
                .bind(notificationRetryQueue())
                .to(notificationMainExchange())
                .with("notification.main.routing.key");
    }
    
    // Binding for dead letter queue
    @Bean
    public Binding notificationDeadLetterBinding() {
        return BindingBuilder
                .bind(notificationDeadLetterQueue())
                .to(notificationDeadLetterExchange())
                .with("notification.dlq.routing.key");
    }
}