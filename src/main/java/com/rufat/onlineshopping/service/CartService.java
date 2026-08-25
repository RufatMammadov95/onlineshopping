package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.AddToCartRequest;
import com.rufat.onlineshopping.dto.CartItemDto;
import com.rufat.onlineshopping.entity.*;
import com.rufat.onlineshopping.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
			ProductRepository productRepository, UserRepository userRepository) {
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public String addToCart(String username, AddToCartRequest request) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new RuntimeException("Məhsul tapılmadı"));

		if (product.getStockQuantity() < request.getQuantity()) {
			throw new RuntimeException("Anbarda kifayət qədər məhsul yoxdur!");
		}

		Cart cart = cartRepository.findByUserId(user.getId())
				.orElseGet(() -> cartRepository.save(Cart.builder().user(user).items(new ArrayList<>()).build()));

		Optional<CartItem> existingItem = cart.getItems().stream()
				.filter(item -> item.getProduct().getId().equals(product.getId())).findFirst();

		if (existingItem.isPresent()) {
			CartItem item = existingItem.get();
			item.setQuantity(item.getQuantity() + request.getQuantity());
			cartItemRepository.save(item);
		} else {
			CartItem newItem = CartItem.builder().cart(cart).product(product).quantity(request.getQuantity()).build();
			cartItemRepository.save(newItem);
		}

		return "Məhsul səbətə əlavə olundu!";
	}

	public List<CartItemDto> getCartItems(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

		Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Səbət boşdur"));

		return cart.getItems().stream().map(item -> {
			CartItemDto dto = new CartItemDto();
			dto.setId(item.getId());
			dto.setProductId(item.getProduct().getId());
			dto.setProductName(item.getProduct().getName());
			dto.setProductPrice(item.getProduct().getPrice());
			dto.setQuantity(item.getQuantity());
			return dto;
		}).collect(Collectors.toList());
	}

	@Transactional
	public void removeFromCart(String username, Long itemId) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

		Cart cart = cartRepository.findByUserId(user.getId())
				.orElseThrow(() -> new RuntimeException("Səbət tapılmadı"));

		CartItem cartItem = cartItemRepository.findById(itemId)
				.orElseThrow(() -> new RuntimeException("Səbətdə belə məhsul tapılmadı"));

		if (!cartItem.getCart().getId().equals(cart.getId())) {
			throw new RuntimeException("Bu məhsulu silməyə icazəniz yoxdur!");
		}

		cartItemRepository.delete(cartItem);
	}

	@Transactional
	public void clearCart(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

		Cart cart = cartRepository.findByUserId(user.getId())
				.orElseThrow(() -> new RuntimeException("Səbət tapılmadı"));

		cartItemRepository.deleteAll(cart.getItems());
		cart.getItems().clear();
	}
}