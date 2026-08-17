package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.ProductDto;
import com.rufat.onlineshopping.entity.Category;
import com.rufat.onlineshopping.entity.Product;
import com.rufat.onlineshopping.repository.CategoryRepository;
import com.rufat.onlineshopping.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	public ProductDto createProduct(ProductDto dto) {
		Category category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı!"));

		Product product = Product.builder().name(dto.getName()).description(dto.getDescription()).price(dto.getPrice())
				.stockQuantity(dto.getStockQuantity()).imageUrl(dto.getImageUrl()).category(category).build();

		Product saved = productRepository.save(product);
		dto.setId(saved.getId());
		return dto;
	}

	public List<ProductDto> getAllProducts() {
		return productRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
	}

	public List<ProductDto> getProductsByCategory(Long categoryId) {
		return productRepository.findByCategoryId(categoryId).stream().map(this::mapToDto).collect(Collectors.toList());
	}

	public List<ProductDto> searchProducts(String name) {
		return productRepository.findByNameContainingIgnoreCase(name).stream().map(this::mapToDto)
				.collect(Collectors.toList());
	}

	private ProductDto mapToDto(Product product) {
		ProductDto dto = new ProductDto();
		dto.setId(product.getId());
		dto.setName(product.getName());
		dto.getDescription();
		dto.setDescription(product.getDescription());
		dto.setPrice(product.getPrice());
		dto.setStockQuantity(product.getStockQuantity());
		dto.setImageUrl(product.getImageUrl());
		dto.setCategoryId(product.getCategory().getId());
		return dto;
	}
}