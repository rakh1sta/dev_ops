package com.agricultural.statisticsservice.client;

import com.agricultural.statisticsservice.client.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "order-service", path = "/api/orders")
public interface OrderServiceClient {
    
    @GetMapping
    List<OrderDTO> getAllOrders();
    
    @GetMapping("/date-range")
    List<OrderDTO> getOrdersByDateRange(@RequestParam("startDate") LocalDate startDate, 
                                        @RequestParam("endDate") LocalDate endDate);
    
    @GetMapping("/customer/{customerId}")
    List<OrderDTO> getOrdersByCustomer(@RequestParam("customerId") Long customerId);
}