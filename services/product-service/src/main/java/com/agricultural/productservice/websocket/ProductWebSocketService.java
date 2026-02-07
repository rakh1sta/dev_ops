package com.agricultural.productservice.websocket;

import com.agricultural.productservice.dto.ProductUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductWebSocketService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public void sendProductUpdateNotification(ProductUpdateDTO productUpdate) {
        messagingTemplate.convertAndSend("/topic/product-updates", productUpdate);
    }
    
    public void sendPriceChangeNotification(ProductUpdateDTO productUpdate) {
        messagingTemplate.convertAndSend("/topic/price-changes", productUpdate);
    }
    
    public void sendQuantityChangeNotification(ProductUpdateDTO productUpdate) {
        messagingTemplate.convertAndSend("/topic/quantity-changes", productUpdate);
    }
}