package com.rufat.onlineshopping.controller;

import com.rufat.onlineshopping.dto.CreateOrderRequest;
import com.rufat.onlineshopping.dto.OrderResponseDto;
import com.rufat.onlineshopping.dto.UpdateOrderStatusRequest;
import com.rufat.onlineshopping.dto.CancellationRequestDto;
import com.rufat.onlineshopping.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<OrderResponseDto> createOrder(Authentication authentication,
			@Valid @RequestBody CreateOrderRequest request) {
		return ResponseEntity.ok(orderService.createOrder(authentication.getName(), request));
	}

	@GetMapping
	public ResponseEntity<List<OrderResponseDto>> getOrders(Authentication authentication) {
		return ResponseEntity.ok(orderService.getOrders(authentication.getName()));
	}

	@PatchMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<OrderResponseDto> updateStatus(@PathVariable Long id,
			@Valid @RequestBody UpdateOrderStatusRequest request) {
		return ResponseEntity.ok(orderService.updateStatus(id, request.getStatus()));
	}

	@PostMapping("/{id}/cancellation-request")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<OrderResponseDto> requestCancellation(Authentication authentication, @PathVariable Long id,
			@Valid @RequestBody CancellationRequestDto request) {
		return ResponseEntity.ok(orderService.requestCancellation(authentication.getName(), id, request.getReason()));
	}
}
