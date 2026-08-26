package com.rufat.onlineshopping.dto;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class ProductDto {
	private Long id;
	@NotBlank(message = "Product name is required")
	@Size(max = 255, message = "Product name must not exceed 255 characters")
	private String name;
	@Size(max = 1000, message = "Description must not exceed 1000 characters")
	private String description;
	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
	private BigDecimal price;
	@NotNull(message = "Stock quantity is required")
	@PositiveOrZero(message = "Stock quantity cannot be negative")
	private Integer stockQuantity;
	private String imageUrl;
	@NotNull(message = "Category ID is required")
	private Long categoryId;
	private String categoryName;
	private List<Long> categoryIds;
	private List<String> categoryNames;
	private boolean available;
}
