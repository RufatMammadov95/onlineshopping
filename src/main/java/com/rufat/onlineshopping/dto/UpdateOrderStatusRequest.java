package com.rufat.onlineshopping.dto;

import com.rufat.onlineshopping.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
	@NotNull(message = "Order status is required")
	private OrderStatus status;
}
