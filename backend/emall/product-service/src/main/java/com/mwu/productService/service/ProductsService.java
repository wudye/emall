package com.mwu.productService.service;

import com.mwu.productService.DTO.ProductDTO;
import com.mwu.productService.DTO.ProductResponse;


public interface ProductsService {

    ProductDTO addProducts(Long categoryId, ProductDTO productDto);

    ProductResponse viewAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword, String category);

    ProductDTO viewProductsById(Long id);

    ProductDTO updateProducts(ProductDTO productDto, Long id);

    ProductDTO deleteProducts(Long id);

    ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
                                     String sortOrder);

    ProductResponse searchProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
                                            String sortOrder);

    ProductDTO updateProductImageReference(Long productId, String imageId);

    String getProductImageId(Long productId);

}
