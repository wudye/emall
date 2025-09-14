package com.mwu.cartService.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

    private Long cartItemId;
    @JsonIgnore
    private CartDTO cartDto;
    @JsonIgnore
    private ProductDTO productDto;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
