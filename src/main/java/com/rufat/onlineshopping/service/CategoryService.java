package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.CategoryDto;
import com.rufat.onlineshopping.entity.Category;
import com.rufat.onlineshopping.repository.CategoryRepository;
import com.rufat.onlineshopping.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;

	public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
		this.categoryRepository = categoryRepository;
		this.productRepository = productRepository;
	}

	@CacheEvict(value = "categories", allEntries = true)
	public CategoryDto createCategory(CategoryDto dto) {
		Category category = Category.builder().name(dto.getName()).description(dto.getDescription()).build();

		Category saved = categoryRepository.save(category);
		dto.setId(saved.getId());
		return dto;
	}

	@Cacheable("categories")
	public List<CategoryDto> getAllCategories() {
		return categoryRepository.findAll().stream().map(cat -> {
			CategoryDto dto = new CategoryDto();
			dto.setId(cat.getId());
			dto.setName(cat.getName());
			dto.setDescription(cat.getDescription());
			return dto;
		}).collect(Collectors.toList());
	}

	@Transactional
	@CacheEvict(value = "categories", allEntries = true)
	public void deleteCategory(Long id) {
		if (!categoryRepository.existsById(id)) {
			throw new RuntimeException("Kateqoriya tapılmadı!");
		}
		if (productRepository.existsByCategoryId(id)) {
			throw new RuntimeException("Məhsulu olan kateqoriya silinə bilməz!");
		}
		categoryRepository.deleteById(id);
	}

	@Transactional
	@CacheEvict(value = "categories", allEntries = true)
	public CategoryDto updateCategory(Long id, CategoryDto dto) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Category not found"));
		category.setName(dto.getName());
		category.setDescription(dto.getDescription());
		Category updated = categoryRepository.save(category);
		CategoryDto result = new CategoryDto();
		result.setId(updated.getId());
		result.setName(updated.getName());
		result.setDescription(updated.getDescription());
		return result;
	}
}
