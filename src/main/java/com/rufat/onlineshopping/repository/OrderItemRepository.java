package com.rufat.onlineshopping.repository;

import com.rufat.onlineshopping.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rufat.onlineshopping.entity.Product;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	boolean existsByProduct(Product product);
}
