package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.ProductDto;
import com.rufat.onlineshopping.entity.Category;
import com.rufat.onlineshopping.entity.Product;
import com.rufat.onlineshopping.repository.CategoryRepository;
import com.rufat.onlineshopping.repository.ProductRepository;
import com.rufat.onlineshopping.repository.CartItemRepository;
import com.rufat.onlineshopping.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final CartItemRepository cartItemRepository;
	private final OrderItemRepository orderItemRepository;

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
			CartItemRepository cartItemRepository, OrderItemRepository orderItemRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.cartItemRepository = cartItemRepository;
		this.orderItemRepository = orderItemRepository;
	}

	@Transactional
	@CacheEvict(value = "products", allEntries = true)
	public ProductDto createProduct(ProductDto dto) {
		Category category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı!"));

		List<Category> categories = resolveCategories(dto, category);
		Product product = Product.builder().name(dto.getName()).description(dto.getDescription()).price(dto.getPrice())
				.stockQuantity(dto.getStockQuantity()).imageUrl(dto.getImageUrl()).category(category)
				.categories(categories).build();

		Product saved = productRepository.save(product);
		dto.setId(saved.getId());
		return dto;
	}

	@Cacheable("products")
	@Transactional(readOnly = true)
	public List<ProductDto> getAllProducts() {
		return productRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ProductDto> getProductsByCategory(Long categoryId) {
		return productRepository.findByCategoryId(categoryId).stream().map(this::mapToDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ProductDto> searchProducts(String name) {
		return productRepository.findByNameContainingIgnoreCase(name).stream().map(this::mapToDto)
				.collect(Collectors.toList());
	}

	@Transactional
	@CacheEvict(value = "products", allEntries = true)
	public ProductDto updateProduct(Long id, ProductDto dto) {
		Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Məhsul tapılmadı!"));

		if (dto.getCategoryId() != null) {
			Category category = categoryRepository.findById(dto.getCategoryId())
					.orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı!"));
			product.setCategory(category);
			product.setCategories(resolveCategories(dto, category));
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
	@CacheEvict(value = "products", allEntries = true)
	public void deleteProduct(Long id) {
		if (!productRepository.existsById(id)) {
			throw new RuntimeException("Məhsul tapılmadı!");
		}
		Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Məhsul tapılmadı!"));
		if (cartItemRepository.existsByProduct(product) || orderItemRepository.existsByProduct(product)) {
			throw new RuntimeException(
					"Bu məhsul səbətdə və ya sifariş tarixçəsində istifadə olunduğu üçün silinə bilməz. Məhsulu stokdan çıxarmaq üçün stokunu 0 edin.");
		}
		productRepository.delete(product);
	}

	private ProductDto mapToDto(Product product) {
		ProductDto dto = new ProductDto();
		dto.setId(product.getId());
		dto.setName(product.getName());
		dto.setDescription(product.getDescription());
		dto.setPrice(product.getPrice());
		dto.setStockQuantity(product.getStockQuantity());
		dto.setAvailable(product.getStockQuantity() != null && product.getStockQuantity() > 0);
		dto.setImageUrl(product.getImageUrl());
		if (product.getCategory() != null) {
			dto.setCategoryId(product.getCategory().getId());
			dto.setCategoryName(product.getCategory().getName());
		}
		if (product.getCategories() != null && !product.getCategories().isEmpty()) {
			dto.setCategoryIds(product.getCategories().stream().map(Category::getId).toList());
			dto.setCategoryNames(product.getCategories().stream().map(Category::getName).toList());
		}
		return dto;
	}

	private List<Category> resolveCategories(ProductDto dto, Category fallback) {
		if (dto.getCategoryIds() == null || dto.getCategoryIds().isEmpty())
			return new ArrayList<>(List.of(fallback));
		List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
		if (categories.size() != dto.getCategoryIds().size())
			throw new RuntimeException("Kateqoriyalardan biri tapılmadı!");
		return categories;
	}
}
