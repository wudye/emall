package com.mwu.cartService.service;

import com.mwu.cartService.DTO.CartDTO;

import java.util.List;

public interface CartService {

    CartDTO addProductsToCart(Long productId, int quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart(Long profileId, Long cartId);

    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);

    String deleteUserProductsByEmail(String email);
}

