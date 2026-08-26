package com.rufat.onlineshopping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CancellationRequestDto {
	@NotBlank(message = "Cancellation reason is required")
	@Size(max = 1000, message = "Cancellation reason must not exceed 1000 characters")
	private String reason;
}
