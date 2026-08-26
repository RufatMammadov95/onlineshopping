package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.CreateOrderRequest;
import com.rufat.onlineshopping.dto.OrderItemResponseDto;
import com.rufat.onlineshopping.dto.OrderResponseDto;
import com.rufat.onlineshopping.entity.*;
import com.rufat.onlineshopping.repository.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;

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
	@CacheEvict(value = "products", allEntries = true)
	public OrderResponseDto createOrder(String username, CreateOrderRequest request) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
		Cart cart = cartRepository.findByUserId(user.getId())
				.orElseThrow(() -> new RuntimeException("Səbətiniz boşdur, sifariş yaradıla bilməz!"));
		if (cart.getItems().isEmpty())
			throw new RuntimeException("Səbətdə heç bir məhsul yoxdur!");
		Order order = Order.builder().user(user).shippingAddress(request.getShippingAddress())
				.status(OrderStatus.PENDING).customerFirstName(request.getFirstName())
				.customerLastName(request.getLastName()).customerPhone(request.getPhone())
				.customerEmail(request.getEmail()).paymentMethod(request.getPaymentMethod()).build();
		List<OrderItem> items = new ArrayList<>();
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (CartItem cartItem : cart.getItems()) {
			Product product = cartItem.getProduct();
			if (product.getStockQuantity() < cartItem.getQuantity())
				throw new RuntimeException("Məhsul (" + product.getName() + ") üçün yetərli stok yoxdur!");
			product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
			productRepository.save(product);
			totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
			items.add(OrderItem.builder().order(order).product(product).quantity(cartItem.getQuantity())
					.price(product.getPrice()).build());
		}
		order.setTotalPrice(totalPrice);
		order.setItems(items);
		Order savedOrder = orderRepository.save(order);
		cartItemRepository.deleteAll(cart.getItems());
		cart.getItems().clear();
		return mapToDto(savedOrder);
	}

	@Transactional(readOnly = true)
	public List<OrderResponseDto> getOrders(String username) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
		List<Order> orders = user.getRole() == Role.ADMIN ? orderRepository.findAll()
				: orderRepository.findByUserId(user.getId());
		return orders.stream().map(this::mapToDto).collect(Collectors.toList());
	}

	/**
	 * Backwards-compatible alias for callers that need only the authenticated
	 * user's order history. New HTTP code should use {@link #getOrders(String)}.
	 */
	@Transactional(readOnly = true)
	public List<OrderResponseDto> getUserOrders(String username) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
		return orderRepository.findByUserId(user.getId()).stream().map(this::mapToDto).collect(Collectors.toList());
	}

	@Transactional
	public OrderResponseDto updateStatus(Long orderId, OrderStatus status) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
		validateStatusTransition(order.getStatus(), status);
		order.setStatus(status);
		return mapToDto(orderRepository.save(order));
	}

	@Transactional
	public OrderResponseDto requestCancellation(String username, Long orderId, String reason) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
		if (!order.getUser().getId().equals(user.getId()))
			throw new RuntimeException("You can only cancel your own orders");
		if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED)
			throw new RuntimeException("This order can no longer be cancelled");
		if (order.isCancellationRequested())
			throw new RuntimeException("Cancellation request already exists");
		order.setCancellationRequested(true);
		order.setCancellationReason(reason);
		order.setCancellationRequestedAt(java.time.LocalDateTime.now());
		return mapToDto(orderRepository.save(order));
	}

	private void validateStatusTransition(OrderStatus current, OrderStatus next) {
		if (current == next)
			return;
		boolean valid = (current == OrderStatus.PENDING
				&& (next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED))
				|| (current == OrderStatus.SHIPPED && (next == OrderStatus.DELIVERED || next == OrderStatus.CANCELLED));
		if (!valid)
			throw new RuntimeException("Invalid order status transition");
	}

	private OrderResponseDto mapToDto(Order order) {
		OrderResponseDto dto = new OrderResponseDto();
		dto.setId(order.getId());
		dto.setTotalPrice(order.getTotalPrice());
		dto.setStatus(order.getStatus());
		dto.setShippingAddress(order.getShippingAddress());
		dto.setCreatedAt(order.getCreatedAt());
		dto.setCustomerUsername(order.getUser().getUsername());
		dto.setCustomerEmail(order.getUser().getEmail());
		dto.setCancellationRequested(order.isCancellationRequested());
		dto.setCancellationReason(order.getCancellationReason());
		dto.setCancellationRequestedAt(order.getCancellationRequestedAt());
		dto.setItems((order.getItems() == null ? List.<OrderItem>of() : order.getItems()).stream().map(item -> {
			OrderItemResponseDto itemDto = new OrderItemResponseDto();
			itemDto.setProductId(item.getProduct().getId());
			itemDto.setProductName(item.getProduct().getName());
			itemDto.setQuantity(item.getQuantity());
			itemDto.setUnitPrice(item.getPrice());
			return itemDto;
		}).collect(Collectors.toList()));
		return dto;
	}
}
