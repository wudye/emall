package com.mwu.productService.config;

import com.mwu.productService.DTO.CategoryDTO;
import com.mwu.productService.DTO.ProductDTO;
import com.mwu.productService.entity.Category;
import com.mwu.productService.entity.Products;
import org.mapstruct.Mapper;

@Mapper
public interface MapperStructConfig {
    CategoryDTO toCategoryDTO(Category category);
    Category toCategory(CategoryDTO categoryDTO);

    ProductDTO toProductDTO( Products product);
    Products toProduct(ProductDTO productDTO);


}
