package com.mwu.cartService.client;

import com.mwu.cartService.DTO.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service", url = "http://localhost:8002")
// Using Feign client to communicate with product service
public interface ProductClient {

    @GetMapping("/api/public/products/{productId}")
    ProductDTO getProductById(@PathVariable Long productId, @RequestHeader("Authorization") String authHeader);

    @PutMapping("/api/products")
    void updateProduct(@RequestBody ProductDTO productDto);
}
