package com.rufat.onlineshopping.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(length = 1000)
	private String description;

	@Column(nullable = false)
	private BigDecimal price;

	@Column(name = "stock_quantity", nullable = false)
	private Integer stockQuantity;

	@Column(name = "image_url", columnDefinition = "TEXT")
	private String imageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
	private List<Category> categories;
}
