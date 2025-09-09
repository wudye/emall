package com.mwu.productService.repository;

import com.mwu.productService.entity.Category;
import com.mwu.productService.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long>, JpaSpecificationExecutor<Products> {

    Page<Products> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetails);

    Page<Products> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);

}
