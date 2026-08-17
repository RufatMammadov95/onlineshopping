package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.CreateOrderRequest;
import com.rufat.onlineshopping.dto.OrderResponseDto;
import com.rufat.onlineshopping.entity.*;
import com.rufat.onlineshopping.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	public OrderService(OrderRepository orderRepository, CartRepository cartRepository,
			CartItemRepository cartItemRepository, UserRepository userRepository, ProductRepository productRepository) {
		this.orderRepository = orderRepository;
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.userRepository = userRepository;
		this.productRepository = productRepository;
	}

	@Transactional
	public OrderResponseDto createOrder(String username, CreateOrderRequest request) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

		Cart cart = cartRepository.findByUserId(user.getId())
				.orElseThrow(() -> new RuntimeException("Səbətiniz boşdur, sifariş yaradıla bilməz!"));

		if (cart.getItems().isEmpty()) {
			throw new RuntimeException("Səbətdə heç bir məhsul yoxdur!");
		}

		BigDecimal totalPrice = BigDecimal.ZERO;
		List<OrderItem> orderItems = new ArrayList<>();

		Order order = Order.builder().user(user).shippingAddress(request.getShippingAddress())
				.status(OrderStatus.PENDING).build();

		for (CartItem cartItem : cart.getItems()) {
			Product product = cartItem.getProduct();

			if (product.getStockQuantity() < cartItem.getQuantity()) {
				throw new RuntimeException("Məhsul (" + product.getName() + ") üçün yetərli stok yoxdur!");
			}

			product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
			productRepository.save(product);

			BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
			totalPrice = totalPrice.add(itemTotal);

			OrderItem orderItem = OrderItem.builder().order(order).product(product).quantity(cartItem.getQuantity())
					.price(product.getPrice()).build();

			orderItems.add(orderItem);
		}

		order.setTotalPrice(totalPrice);
		order.setItems(orderItems);

		Order savedOrder = orderRepository.save(order);

		cartItemRepository.deleteAll(cart.getItems());

		OrderResponseDto dto = new OrderResponseDto();
		dto.setId(savedOrder.getId());
		dto.setTotalPrice(savedOrder.getTotalPrice());
		dto.setStatus(savedOrder.getStatus());
		dto.setShippingAddress(savedOrder.getShippingAddress());
		dto.setCreatedAt(savedOrder.getCreatedAt());

		return dto;
	}

	public List<OrderResponseDto> getUserOrders(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

		return orderRepository.findByUserId(user.getId()).stream().map(order -> {
			OrderResponseDto dto = new OrderResponseDto();
			dto.setId(order.getId());
			dto.setTotalPrice(order.getTotalPrice());
			dto.setStatus(order.getStatus());
			dto.setShippingAddress(order.getShippingAddress());
			dto.setCreatedAt(order.getCreatedAt());
			return dto;
		}).collect(Collectors.toList());
	}
}