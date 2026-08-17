package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.CategoryDto;
import com.rufat.onlineshopping.entity.Category;
import com.rufat.onlineshopping.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public CategoryDto createCategory(CategoryDto dto) {
		Category category = Category.builder().name(dto.getName()).description(dto.getDescription()).build();

		Category saved = categoryRepository.save(category);
		dto.setId(saved.getId());
		return dto;
	}

	public List<CategoryDto> getAllCategories() {
		return categoryRepository.findAll().stream().map(cat -> {
			CategoryDto dto = new CategoryDto();
			dto.setId(cat.getId());
			dto.setName(cat.getName());
			dto.setDescription(cat.getDescription());
			return dto;
		}).collect(Collectors.toList());
	}
}