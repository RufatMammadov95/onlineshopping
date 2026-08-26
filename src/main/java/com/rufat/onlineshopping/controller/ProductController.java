package com.rufat.onlineshopping.controller;

import com.rufat.onlineshopping.dto.ProductDto;
import com.rufat.onlineshopping.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto dto) {
		return ResponseEntity.ok(productService.createProduct(dto));
	}

	@GetMapping
	public ResponseEntity<List<ProductDto>> getAllProducts(Authentication authentication) {
		return ResponseEntity.ok(forViewer(productService.getAllProducts(), authentication));
	}

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable Long categoryId, Authentication authentication) {
		return ResponseEntity.ok(forViewer(productService.getProductsByCategory(categoryId), authentication));
	}

	@GetMapping("/search")
	public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String name, Authentication authentication) {
		return ResponseEntity.ok(forViewer(productService.searchProducts(name), authentication));
	}

	private List<ProductDto> forViewer(List<ProductDto> products, Authentication authentication) {
		boolean admin = authentication != null && authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
		if (admin) return products;
		return products.stream().map(p -> {
			ProductDto copy = new ProductDto();
			copy.setId(p.getId()); copy.setName(p.getName()); copy.setDescription(p.getDescription()); copy.setPrice(p.getPrice());
			copy.setImageUrl(p.getImageUrl()); copy.setCategoryId(p.getCategoryId()); copy.setCategoryName(p.getCategoryName());
			copy.setCategoryIds(p.getCategoryIds()); copy.setCategoryNames(p.getCategoryNames());
			copy.setAvailable(p.isAvailable());
			return copy;
		}).toList();
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
		return ResponseEntity.ok(productService.updateProduct(id, dto));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		return ResponseEntity.ok("Məhsul uğurla silindi.");
	}
}
