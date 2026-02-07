package com.agricultural.productservice.service;

import com.agricultural.productservice.dto.ProductDTO;
import com.agricultural.productservice.dto.PriceHistoryDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    
    List<ProductDTO> getAllProducts();
    
    ProductDTO getProductById(Long id);
    
    ProductDTO createProduct(ProductDTO productDTO);
    
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    
    void deleteProduct(Long id);
    
    List<ProductDTO> getProductsByCategory(Long categoryId);
    
    List<ProductDTO> searchProductsByName(String name);
    
    // Inventory Management
    ProductDTO updateProductQuantity(Long id, Integer quantity);
    
    // Price Tracker
    ProductDTO updateProductPrice(Long id, BigDecimal newPrice);
    
    List<PriceHistoryDTO> getPriceHistoryByProduct(Long productId);
}