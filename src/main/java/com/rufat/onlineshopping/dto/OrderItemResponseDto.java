package com.rufat.onlineshopping.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemResponseDto {
	private Long productId;
	private String productName;
	private Integer quantity;
	private BigDecimal unitPrice;
}
