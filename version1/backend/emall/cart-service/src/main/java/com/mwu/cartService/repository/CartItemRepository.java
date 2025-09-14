package com.mwu.cartService.repository;

import com.mwu.cartService.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByProductIdAndCart_CartId(Long productId, Long cartId);
    List<CartItem> findByCart_CartIdAndProductIdNot(Long cartId, Long productId);
    void deleteByCart_CartId(Long cartId);
}
