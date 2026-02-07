package com.agricultural.productservice.controller;

import com.agricultural.productservice.dto.ProductDTO;
import com.agricultural.productservice.dto.PriceHistoryDTO;
import com.agricultural.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLIER')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.ok(createdProduct);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLIER')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLIER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductDTO> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProductsByName(@RequestParam String name) {
        List<ProductDTO> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }
    
    @PatchMapping("/{id}/quantity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLIER')")
    public ResponseEntity<ProductDTO> updateProductQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        ProductDTO updatedProduct = productService.updateProductQuantity(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @PatchMapping("/{id}/price")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLIER')")
    public ResponseEntity<ProductDTO> updateProductPrice(@PathVariable Long id, @RequestParam BigDecimal newPrice) {
        ProductDTO updatedProduct = productService.updateProductPrice(id, newPrice);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @GetMapping("/{id}/price-history")
    public ResponseEntity<List<PriceHistoryDTO>> getPriceHistoryByProduct(@PathVariable Long id) {
        List<PriceHistoryDTO> priceHistories = productService.getPriceHistoryByProduct(id);
        return ResponseEntity.ok(priceHistories);
    }
}