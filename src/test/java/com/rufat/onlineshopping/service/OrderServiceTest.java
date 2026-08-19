package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.CreateOrderRequest;
import com.rufat.onlineshopping.dto.OrderResponseDto;
import com.rufat.onlineshopping.entity.*;
import com.rufat.onlineshopping.repository.*;
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
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private CartRepository cartRepository;

	@Mock
	private CartItemRepository cartItemRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private OrderService orderService;

	private User user;
	private Product product;
	private Cart cart;
	private CartItem cartItem;
	private CreateOrderRequest createOrderRequest;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1L);
		user.setUsername("rufat");

		product = Product.builder().id(10L).name("Noutbuk").price(BigDecimal.valueOf(2000)).stockQuantity(5).build();

		cart = Cart.builder().id(100L).user(user).items(new ArrayList<>()).build();

		cartItem = CartItem.builder().id(1000L).cart(cart).product(product).quantity(2).build();

		cart.getItems().add(cartItem);

		createOrderRequest = new CreateOrderRequest();
		createOrderRequest.setShippingAddress("Baku, Nizami str.");
	}

	@Test
	void createOrder_WhenCartHasItems_ShouldCreateOrderSuccessfully() {
		// Arrange
		Order savedOrder = Order.builder().id(50L).user(user).shippingAddress("Baku, Nizami str.")
				.status(OrderStatus.PENDING).totalPrice(BigDecimal.valueOf(4000)).build();

		when(userRepository.findByUsername("rufat")).thenReturn(Optional.of(user));
		when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
		when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

		// Act
		OrderResponseDto response = orderService.createOrder("rufat", createOrderRequest);

		// Assert
		assertNotNull(response);
		assertEquals(50L, response.getId());
		assertEquals(BigDecimal.valueOf(4000), response.getTotalPrice());
		assertEquals(OrderStatus.PENDING, response.getStatus());
		assertEquals("Baku, Nizami str.", response.getShippingAddress());

		assertEquals(3, product.getStockQuantity());
		verify(productRepository, times(1)).save(product);
		verify(cartItemRepository, times(1)).deleteAll(cart.getItems());
		verify(orderRepository, times(1)).save(any(Order.class));
	}

	@Test
	void createOrder_WhenCartIsEmpty_ShouldThrowException() {
		// Arrange
		cart.getItems().clear();
		when(userRepository.findByUsername("rufat")).thenReturn(Optional.of(user));
		when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			orderService.createOrder("rufat", createOrderRequest);
		});

		assertEquals("Səbətdə heç bir məhsul yoxdur!", exception.getMessage());
		verify(orderRepository, never()).save(any(Order.class));
	}

	@Test
	void createOrder_WhenStockIsInsufficient_ShouldThrowException() {
		// Arrange
		cartItem.setQuantity(10);
		when(userRepository.findByUsername("rufat")).thenReturn(Optional.of(user));
		when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			orderService.createOrder("rufat", createOrderRequest);
		});

		assertTrue(exception.getMessage().contains("yetərli stok yoxdur"));
		verify(orderRepository, never()).save(any(Order.class));
	}

	@Test
	void getUserOrders_ShouldReturnOrderList() {
		// Arrange
		Order order = Order.builder().id(50L).user(user).shippingAddress("Baku").status(OrderStatus.PENDING)
				.totalPrice(BigDecimal.valueOf(4000)).build();

		when(userRepository.findByUsername("rufat")).thenReturn(Optional.of(user));
		when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));

		// Act
		List<OrderResponseDto> orders = orderService.getUserOrders("rufat");

		// Assert
		assertNotNull(orders);
		assertEquals(1, orders.size());
		assertEquals(50L, orders.get(0).getId());
		verify(orderRepository, times(1)).findByUserId(1L);
	}
}