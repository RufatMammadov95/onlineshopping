package com.rufat.onlineshopping.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserProfileDto {
	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	private String username;
	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	@Size(max = 255, message = "Email must not exceed 255 characters")
	private String email;
	@Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
	@Pattern(regexp = "^\\S+$", message = "Password must not contain spaces")
	private String password;
}
