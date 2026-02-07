package com.agricultural.orderservice.service.impl;

import com.agricultural.orderservice.client.ProductClient;
import com.agricultural.orderservice.client.UserClient;
import com.agricultural.orderservice.client.dto.UserDTO;
import com.agricultural.orderservice.config.RabbitMQConfig;
import com.agricultural.orderservice.dto.CreateOrderRequest;
import com.agricultural.orderservice.dto.OrderDTO;
import com.agricultural.orderservice.dto.OrderEvent;
import com.agricultural.orderservice.dto.TransactionDTO;
import com.agricultural.orderservice.entity.Order;
import com.agricultural.orderservice.entity.OrderItem;
import com.agricultural.orderservice.entity.Transaction;
import com.agricultural.orderservice.entity.Order.OrderStatus;
import com.agricultural.orderservice.entity.Transaction.PaymentStatus;
import com.agricultural.orderservice.mapper.OrderMapper;
import com.agricultural.orderservice.mapper.TransactionMapper;
import com.agricultural.orderservice.repository.OrderItemRepository;
import com.agricultural.orderservice.repository.OrderRepository;
import com.agricultural.orderservice.repository.TransactionRepository;
import com.agricultural.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TransactionRepository transactionRepository;
    private final OrderMapper orderMapper;
    private final TransactionMapper transactionMapper;
    private final ProductClient productClient;
    private final UserClient userClient;
    private final RabbitTemplate rabbitTemplate;
    
    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return orderMapper.toDto(order);
    }
    
    @Override
    public OrderDTO createOrder(CreateOrderRequest request) {
        // Validate product availability and create order
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);
        
        // Create order items and validate inventory
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getOrderItems()) {
            // Check product availability
            ProductDTO product = productClient.getProductById(itemRequest.getProductId());
            if (product.getQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient inventory for product: " + product.getName() + 
                                         ". Available: " + product.getQuantity() + 
                                         ", Requested: " + itemRequest.getQuantity());
            }
            
            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProductId(itemRequest.getProductId());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(itemRequest.getPrice());
            
            orderItemRepository.save(orderItem);
        }
        
        // Create initial transaction
        Transaction transaction = new Transaction();
        transaction.setOrder(savedOrder);
        transaction.setPaymentStatus(PaymentStatus.PENDING);
        transaction.setAmount(request.getTotalAmount());
        transaction.setTransactionDate(LocalDateTime.now());
        
        transactionRepository.save(transaction);
        
        // Send order event to notification service
        try {
            UserDTO user = userClient.getUserById(request.getCustomerId());
            OrderEvent orderEvent = OrderEvent.fromOrderAndCustomerEmail(savedOrder, user.getEmail());
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, orderEvent);
        } catch (Exception e) {
            // Log the error but don't fail the order creation
            System.err.println("Failed to send order event to notification service: " + e.getMessage());
        }
        
        return orderMapper.toDto(savedOrder);
    }
    
    @Override
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        orderMapper.updateEntityFromDto(orderDTO, existingOrder);
        Order updatedOrder = orderRepository.save(existingOrder);
        return orderMapper.toDto(updatedOrder);
    }
    
    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }
    
    @Override
    public List<OrderDTO> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<OrderDTO> getOrdersByStatus(String status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        return orderRepository.findByStatus(orderStatus).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public TransactionDTO createTransaction(Long orderId, TransactionDTO transactionDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        Transaction transaction = new Transaction();
        transaction.setOrder(order);
        transaction.setPaymentStatus(transactionDTO.getPaymentStatus());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setTransactionDate(transactionDTO.getTransactionDate() != null ? 
                                    transactionDTO.getTransactionDate() : LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Send transaction event to notification service
        try {
            OrderEvent orderEvent = new OrderEvent();
            orderEvent.setOrderId(orderId);
            orderEvent.setCustomerId(order.getCustomerId());
            orderEvent.setTotalAmount(transaction.getAmount());
            orderEvent.setStatus(order.getStatus());
            
            // Get customer email
            UserDTO user = userClient.getUserById(order.getCustomerId());
            orderEvent.setCustomerEmail(user.getEmail());
            
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, orderEvent);
        } catch (Exception e) {
            System.err.println("Failed to send transaction event to notification service: " + e.getMessage());
        }
        
        return transactionMapper.toDto(savedTransaction);
    }
    
    @Override
    public List<TransactionDTO> getTransactionsByOrder(Long orderId) {
        return transactionRepository.findByOrderId(orderId).stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public OrderDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        order.setStatus(newStatus);
        
        Order updatedOrder = orderRepository.save(order);
        
        // Send status update event to notification service
        try {
            OrderEvent orderEvent = new OrderEvent();
            orderEvent.setOrderId(id);
            orderEvent.setCustomerId(order.getCustomerId());
            orderEvent.setTotalAmount(order.getTotalAmount());
            orderEvent.setStatus(newStatus);
            
            // Get customer email
            UserDTO user = userClient.getUserById(order.getCustomerId());
            orderEvent.setCustomerEmail(user.getEmail());
            
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, orderEvent);
        } catch (Exception e) {
            System.err.println("Failed to send status update event to notification service: " + e.getMessage());
        }
        
        return orderMapper.toDto(updatedOrder);
    }
}