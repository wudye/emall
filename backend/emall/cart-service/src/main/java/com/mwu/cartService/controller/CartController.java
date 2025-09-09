package com.mwu.cartService.controller;

import com.mwu.cartService.DTO.CartDTO;
import com.mwu.cartService.client.UserClient;
import com.mwu.cartService.entity.Cart;
import com.mwu.cartService.exception.ResourceNotFoundException;
import com.mwu.cartService.repository.CartRepository;
import com.mwu.cartService.service.CartService;
import com.mwu.cartService.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private UserClient userClient;

    @Autowired
    private AuthUtil authUtil;

    // Add Products to Cart
    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductsToCart(@PathVariable Long productId, @PathVariable Integer quantity) {

        CartDTO cartDto = cartService.addProductsToCart(productId, quantity);
        return new ResponseEntity<>(cartDto, HttpStatus.CREATED);
    }

    // Get All Carts
    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts() {

        List<CartDTO> cartDTOs = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTOs, HttpStatus.FOUND);
    }

    // Get Cart by ID
    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById() {

        Long profileId = authUtil.loggedInUserId();
        Cart cart = cartRepo.findCartByProfileId(profileId);
        Long cartId = cart.getCartId();

        CartDTO cartDto = cartService.getCart(profileId, cartId);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    // Update products in cart by passing operations
    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId, @PathVariable String operation) {

        CartDTO cartDto = cartService.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete") ? -1 : 1);

        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    // Delete products in Cart
    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                        @PathVariable Long productId) {
        String status = cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    // Delete Products in cart by email
    @DeleteMapping("/carts/user/{email}/products")
    public ResponseEntity<String> deleteProductsForUserByEmail(@PathVariable String email) {

        String status = cartService.deleteUserProductsByEmail(email);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    // Get cart By Email
    @GetMapping("/carts/user/{email}")
    public ResponseEntity<CartDTO> getCartByEmail(@PathVariable String email) {
        // Get profileId from user service
        Long profileId = userClient.getProfileIdByEmail(email);
        Cart cart = cartRepo.findCartByProfileId(profileId);

        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found for user: " + email);
        }

        CartDTO cartDto = cartService.getCart(profileId, cart.getCartId());
        if (cartDto.getCartItems() == null) {
            cartDto.setCartItems(new ArrayList<>());
        }
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }
}
