package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.ProductDto;
import com.rufat.onlineshopping.entity.Category;
import com.rufat.onlineshopping.entity.Product;
import com.rufat.onlineshopping.repository.CategoryRepository;
import com.rufat.onlineshopping.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Category category;
    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Elektronika");

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("Güclü komputer")
                .price(BigDecimal.valueOf(1500))
                .stockQuantity(10)
                .imageUrl("laptop.jpg")
                .category(category)
                .build();

        productDto = new ProductDto();
        productDto.setName("Laptop");
        productDto.setDescription("Güclü komputer");
        productDto.setPrice(BigDecimal.valueOf(1500));
        productDto.setStockQuantity(10);
        productDto.setImageUrl("laptop.jpg");
        productDto.setCategoryId(1L);
    }

    @Test
    void createProduct_WhenCategoryExists_ShouldReturnCreatedProductDto() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductDto created = productService.createProduct(productDto);

        // Assert
        assertNotNull(created);
        assertEquals(1L, created.getId());
        assertEquals("Laptop", created.getName());
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_WhenCategoryDoesNotExist_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productDto);
        });

        assertEquals("Kateqoriya tapılmadı!", exception.getMessage());
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getAllProducts_ShouldReturnListOfProductDtos() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of(product));

        // Act
        List<ProductDto> products = productService.getAllProducts();

        // Assert
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Laptop", products.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void searchProducts_ShouldReturnMatchingProductDtos() {
        // Arrange
        when(productRepository.findByNameContainingIgnoreCase("lap")).thenReturn(List.of(product));

        // Act
        List<ProductDto> result = productService.searchProducts("lap");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(productRepository, times(1)).findByNameContainingIgnoreCase("lap");
    }
}