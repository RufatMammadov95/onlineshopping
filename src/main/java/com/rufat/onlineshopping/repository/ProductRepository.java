package com.rufat.onlineshopping.repository;

import com.rufat.onlineshopping.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByCategoryId(Long categoryId);

	List<Product> findByNameContainingIgnoreCase(String name);

	boolean existsByCategoryId(Long categoryId);
}
