package com.rufat.onlineshopping.repository;

import com.rufat.onlineshopping.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rufat.onlineshopping.entity.Product;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	boolean existsByProduct(Product product);
}
