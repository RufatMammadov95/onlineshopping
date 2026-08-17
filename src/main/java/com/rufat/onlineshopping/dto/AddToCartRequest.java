package com.rufat.onlineshopping.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
	private Long productId;
	private Integer quantity;
}