package com.rufat.onlineshopping.controller;

import com.rufat.onlineshopping.dto.AddToCartRequest;
import com.rufat.onlineshopping.dto.CartItemDto;
import com.rufat.onlineshopping.dto.UpdateCartItemRequest;
import com.rufat.onlineshopping.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@PostMapping
	public ResponseEntity<String> addToCart(Authentication authentication, @Valid @RequestBody AddToCartRequest request) {
		return ResponseEntity.ok(cartService.addToCart(authentication.getName(), request));
	}

	@GetMapping
	public ResponseEntity<List<CartItemDto>> getCart(Authentication authentication) {
		return ResponseEntity.ok(cartService.getCartItems(authentication.getName()));
	}

	@PutMapping("/items/{itemId}")
	public ResponseEntity<CartItemDto> updateCartItem(Authentication authentication, @PathVariable Long itemId,
			@Valid @RequestBody UpdateCartItemRequest request) {
		return ResponseEntity.ok(cartService.updateCartItem(authentication.getName(), itemId, request.getQuantity()));
	}

	@DeleteMapping("/items/{itemId}")
	public ResponseEntity<String> removeFromCart(Authentication authentication, @PathVariable Long itemId) {
		cartService.removeFromCart(authentication.getName(), itemId);
		return ResponseEntity.ok("Məhsul səbətdən uğurla silindi.");
	}

	@DeleteMapping("/clear")
	public ResponseEntity<String> clearCart(Authentication authentication) {
		cartService.clearCart(authentication.getName());
		return ResponseEntity.ok("Səbət tamamilə təmizləndi.");
	}
}
