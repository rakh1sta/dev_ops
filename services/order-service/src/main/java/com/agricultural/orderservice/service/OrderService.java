package com.agricultural.orderservice.service;

import com.agricultural.orderservice.dto.CreateOrderRequest;
import com.agricultural.orderservice.dto.OrderDTO;
import com.agricultural.orderservice.dto.TransactionDTO;

import java.util.List;

public interface OrderService {
    
    List<OrderDTO> getAllOrders();
    
    OrderDTO getOrderById(Long id);
    
    OrderDTO createOrder(CreateOrderRequest request);
    
    OrderDTO updateOrder(Long id, OrderDTO orderDTO);
    
    void deleteOrder(Long id);
    
    List<OrderDTO> getOrdersByCustomer(Long customerId);
    
    List<OrderDTO> getOrdersByStatus(String status);
    
    // Transaction Management
    TransactionDTO createTransaction(Long orderId, TransactionDTO transactionDTO);
    
    List<TransactionDTO> getTransactionsByOrder(Long orderId);
    
    // Procurement Logic
    OrderDTO updateOrderStatus(Long id, String status);
}