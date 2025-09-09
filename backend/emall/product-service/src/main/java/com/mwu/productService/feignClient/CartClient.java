package com.mwu.productService.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cart-service")
public interface CartClient {


    @PostMapping("/api/cart/addProducts")
    void addProductsToCart(@RequestParam int userId, @RequestParam Long productId, @RequestParam String productName,
                           @RequestParam String productImage, @RequestParam double price, @RequestParam int quantity);

    @DeleteMapping("/api/cart/deleteProduct")
    void deleteProductFromCart(@RequestParam int userId, @RequestParam Long productId);
}
