package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.AddToCartRequest;
import com.rufat.onlineshopping.dto.CartItemDto;
import com.rufat.onlineshopping.entity.Cart;
import com.rufat.onlineshopping.entity.CartItem;
import com.rufat.onlineshopping.entity.Product;
import com.rufat.onlineshopping.entity.User;
import com.rufat.onlineshopping.repository.CartItemRepository;
import com.rufat.onlineshopping.repository.CartRepository;
import com.rufat.onlineshopping.repository.ProductRepository;
import com.rufat.onlineshopping.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Product product;
    private Cart cart;
    private AddToCartRequest addToCartRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("rufat");

        product = Product.builder()
                .id(10L)
                .name("Telefon")
                .price(BigDecimal.valueOf(1000))
                .stockQuantity(5)
                .build();

        cart = Cart.builder()
                .id(100L)
                .user(user)
                .items(new ArrayList<>())
                .build();

        addToCartRequest = new AddToCartRequest();
        addToCartRequest.setProductId(10L);
        addToCartRequest.setQuantity(2);
    }

    @Test
    void addToCart_WhenNewItem_ShouldSaveNewCartItem() {
        // Arrange
        when(userRepository.findByUsername("rufat")).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        // Act
        String result = cartService.addToCart("rufat", addToCartRequest);

        // Assert
        assertEquals("Məhsul səbətə əlavə olundu!", result);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void addToCart_WhenStockIsInsufficient_ShouldThrowException() {
        // Arrange
        addToCartRequest.setQuantity(10); 
        when(userRepository.findByUsername("rufat")).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.addToCart("rufat", addToCartRequest);
        });

        assertEquals("Anbarda kifayət qədər məhsul yoxdur!", exception.getMessage());
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void getCartItems_WhenCartExists_ShouldReturnCartItemDtoList() {
        // Arrange
        CartItem item = CartItem.builder()
                .id(1000L)
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();
        cart.getItems().add(item);

        when(userRepository.findByUsername("rufat")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        // Act
        List<CartItemDto> items = cartService.getCartItems("rufat");

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Telefon", items.get(0).getProductName());
        assertEquals(2, items.get(0).getQuantity());
    }
}