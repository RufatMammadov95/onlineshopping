package com.rufat.onlineshopping.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Data
public class CreateOrderRequest {
	@NotBlank(message = "Shipping address is required")
	@Size(max = 500, message = "Shipping address must not exceed 500 characters")
	private String shippingAddress;
	@NotBlank(message = "First name is required") private String firstName;
	@NotBlank(message = "Last name is required") private String lastName;
	@NotBlank(message = "Phone number is required") @Size(max = 30) private String phone;
	@NotBlank(message = "Email is required") @jakarta.validation.constraints.Email(message = "Email must be valid") private String email;
	@NotBlank(message = "Payment method is required") @Pattern(regexp = "CARD|PAYPAL", message = "Payment method must be CARD or PAYPAL") private String paymentMethod;
}
