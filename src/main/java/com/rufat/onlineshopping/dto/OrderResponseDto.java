package com.rufat.onlineshopping.dto;

import com.rufat.onlineshopping.entity.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {
	private Long id;
	private BigDecimal totalPrice;
	private OrderStatus status;
	private String shippingAddress;
	private LocalDateTime createdAt;
	private List<OrderItemResponseDto> items;
	private String customerUsername;
	private String customerEmail;
	private boolean cancellationRequested;
	private String cancellationReason;
	private LocalDateTime cancellationRequestedAt;
}
