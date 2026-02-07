package com.agricultural.productservice.service.impl;

import com.agricultural.productservice.dto.ProductDTO;
import com.agricultural.productservice.dto.PriceHistoryDTO;
import com.agricultural.productservice.dto.ProductUpdateDTO;
import com.agricultural.productservice.entity.PriceHistory;
import com.agricultural.productservice.entity.Product;
import com.agricultural.productservice.mapper.PriceHistoryMapper;
import com.agricultural.productservice.mapper.ProductMapper;
import com.agricultural.productservice.repository.PriceHistoryRepository;
import com.agricultural.productservice.repository.ProductRepository;
import com.agricultural.productservice.service.CategoryService;
import com.agricultural.productservice.service.ProductService;
import com.agricultural.productservice.websocket.ProductWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;
    private final PriceHistoryMapper priceHistoryMapper;
    private final ProductWebSocketService productWebSocketService;
    
    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return productMapper.toDto(product);
    }
    
    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        // Set category if provided
        Product product = productMapper.toEntity(productDTO);
        if (productDTO.getCategoryId() != null) {
            var category = categoryService.getCategoryById(productDTO.getCategoryId());
            product.setCategory(com.agricultural.productservice.entity.Category.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .build());
        }
        
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }
    
    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        productMapper.updateEntityFromDto(productDTO, existingProduct);
        
        // Update category if provided
        if (productDTO.getCategoryId() != null) {
            var category = categoryService.getCategoryById(productDTO.getCategoryId());
            existingProduct.setCategory(com.agricultural.productservice.entity.Category.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .build());
        }
        
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDto(updatedProduct);
    }
    
    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
    
    @Override
    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductDTO> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public ProductDTO updateProductQuantity(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        Integer oldQuantity = product.getQuantity();
        product.setQuantity(quantity);
        
        Product updatedProduct = productRepository.save(product);
        
        // Send WebSocket notification for quantity change
        if (!oldQuantity.equals(quantity)) {
            ProductUpdateDTO updateDTO = new ProductUpdateDTO();
            updateDTO.setProductId(product.getId());
            updateDTO.setProductName(product.getName());
            updateDTO.setCategory(product.getCategory() != null ? product.getCategory().getName() : "Unknown");
            updateDTO.setQuantity(quantity);
            updateDTO.setCurrentPrice(product.getCurrentPrice());
            updateDTO.setOldPrice(product.getCurrentPrice()); // No price change
            updateDTO.setChangeType("QUANTITY_CHANGE");
            updateDTO.setTimestamp(LocalDateTime.now());
            
            productWebSocketService.sendQuantityChangeNotification(updateDTO);
            productWebSocketService.sendProductUpdateNotification(updateDTO);
        }
        
        return productMapper.toDto(updatedProduct);
    }
    
    @Override
    public ProductDTO updateProductPrice(Long id, BigDecimal newPrice) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        BigDecimal oldPrice = product.getCurrentPrice();
        
        // Create price history record if price changed
        if (!oldPrice.equals(newPrice)) {
            PriceHistory priceHistory = new PriceHistory();
            priceHistory.setProduct(product);
            priceHistory.setOldPrice(oldPrice);
            priceHistory.setNewPrice(newPrice);
            priceHistory.setChangeDate(LocalDateTime.now());

            priceHistoryRepository.save(priceHistory);
            
            // Send WebSocket notification for price change
            ProductUpdateDTO updateDTO = new ProductUpdateDTO();
            updateDTO.setProductId(product.getId());
            updateDTO.setProductName(product.getName());
            updateDTO.setCategory(product.getCategory() != null ? product.getCategory().getName() : "Unknown");
            updateDTO.setQuantity(product.getQuantity());
            updateDTO.setCurrentPrice(newPrice);
            updateDTO.setOldPrice(oldPrice);
            updateDTO.setChangeType("PRICE_CHANGE");
            updateDTO.setTimestamp(LocalDateTime.now());
            
            productWebSocketService.sendPriceChangeNotification(updateDTO);
            productWebSocketService.sendProductUpdateNotification(updateDTO);
        }

        product.setCurrentPrice(newPrice);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }
    
    @Override
    public List<PriceHistoryDTO> getPriceHistoryByProduct(Long productId) {
        return priceHistoryRepository.findByProductIdOrderByChangeDateDesc(productId).stream()
                .map(priceHistoryMapper::toDto)
                .collect(Collectors.toList());
    }
}