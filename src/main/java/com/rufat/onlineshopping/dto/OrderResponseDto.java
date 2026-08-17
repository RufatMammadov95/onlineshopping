package com.rufat.onlineshopping.dto;

import com.rufat.onlineshopping.entity.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponseDto {
	private Long id;
	private BigDecimal totalPrice;
	private OrderStatus status;
	private String shippingAddress;
	private LocalDateTime createdAt;
}