package com.agricultural.statisticsservice.client;

import com.agricultural.statisticsservice.client.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service", path = "/api/products")
public interface ProductServiceClient {
    
    @GetMapping
    List<ProductDTO> getAllProducts();
    
    @GetMapping("/category/{categoryId}")
    List<ProductDTO> getProductsByCategory(@RequestParam("categoryId") Long categoryId);
    
    @GetMapping("/search")
    List<ProductDTO> searchProductsByName(@RequestParam("name") String name);
}