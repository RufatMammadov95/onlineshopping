package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.CategoryDto;
import com.rufat.onlineshopping.entity.Category;
import com.rufat.onlineshopping.repository.CategoryRepository;
import com.rufat.onlineshopping.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Elektronika")
                .description("Bütün elektronika məhsulları")
                .build();

        categoryDto = new CategoryDto();
        categoryDto.setName("Elektronika");
        categoryDto.setDescription("Bütün elektronika məhsulları");
    }

    @Test
    void createCategory_ShouldReturnCreatedCategoryDto() {
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryDto created = categoryService.createCategory(categoryDto);

        // Assert
        assertNotNull(created);
        assertEquals(1L, created.getId());
        assertEquals("Elektronika", created.getName());
        assertEquals("Bütün elektronika məhsulları", created.getDescription());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategoryDtos() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        // Act
        List<CategoryDto> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Elektronika", result.get(0).getName());
        assertEquals(1L, result.get(0).getId());
        verify(categoryRepository, times(1)).findAll();
    }
}
