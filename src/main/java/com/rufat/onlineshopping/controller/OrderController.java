package com.rufat.onlineshopping.controller;

import com.rufat.onlineshopping.dto.CreateOrderRequest;
import com.rufat.onlineshopping.dto.OrderResponseDto;
import com.rufat.onlineshopping.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	public ResponseEntity<OrderResponseDto> createOrder(Authentication authentication,
			@RequestBody CreateOrderRequest request) {
		return ResponseEntity.ok(orderService.createOrder(authentication.getName(), request));
	}

	@GetMapping
	public ResponseEntity<List<OrderResponseDto>> getUserOrders(Authentication authentication) {
		return ResponseEntity.ok(orderService.getUserOrders(authentication.getName()));
	}
}