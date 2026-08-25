package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.ProductDto;
import com.rufat.onlineshopping.entity.Category;
import com.rufat.onlineshopping.entity.Product;
import com.rufat.onlineshopping.repository.CategoryRepository;
import com.rufat.onlineshopping.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
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

	@Transactional
	public ProductDto updateProduct(Long id, ProductDto dto) {
		Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Məhsul tapılmadı!"));

		if (dto.getCategoryId() != null) {
			Category category = categoryRepository.findById(dto.getCategoryId())
					.orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı!"));
			product.setCategory(category);
		}

		product.setName(dto.getName());
		product.setDescription(dto.getDescription());
		product.setPrice(dto.getPrice());
		product.setStockQuantity(dto.getStockQuantity());
		product.setImageUrl(dto.getImageUrl());

		Product updatedProduct = productRepository.save(product);
		return mapToDto(updatedProduct);
	}

	@Transactional
	public void deleteProduct(Long id) {
		if (!productRepository.existsById(id)) {
			throw new RuntimeException("Məhsul tapılmadı!");
		}
		productRepository.deleteById(id);
	}

	private ProductDto mapToDto(Product product) {
		ProductDto dto = new ProductDto();
		dto.setId(product.getId());
		dto.setName(product.getName());
		dto.setDescription(product.getDescription());
		dto.setPrice(product.getPrice());
		dto.setStockQuantity(product.getStockQuantity());
		dto.setImageUrl(product.getImageUrl());
		if (product.getCategory() != null) {
			dto.setCategoryId(product.getCategory().getId());
		}
		return dto;
	}
}