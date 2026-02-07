package com.agricultural.orderservice.client;

import com.agricultural.orderservice.client.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", path = "/api/products")
public interface ProductClient {
    
    @GetMapping("/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);
}