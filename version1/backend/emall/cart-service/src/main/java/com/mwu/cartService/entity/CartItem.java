package com.mwu.cartService.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private double discount;
    private double productPrice;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
}
