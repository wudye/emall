package com.mwu.productService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long productId;
    private String productName;
    private String imageId;
    private Integer quantity;
    private String description;
    private double price;
    private double discount;
    private double specialPrice;
    private CategoryDTO category;
    private String imageUrl;
}
