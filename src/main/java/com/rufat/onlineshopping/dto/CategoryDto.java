package com.rufat.onlineshopping.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class CategoryDto {
	private Long id;
	@NotBlank(message = "Category name is required")
	@Size(max = 100, message = "Category name must not exceed 100 characters")
	private String name;
	@Size(max = 500, message = "Description must not exceed 500 characters")
	private String description;
}
