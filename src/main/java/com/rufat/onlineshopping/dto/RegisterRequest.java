package com.rufat.onlineshopping.dto;

import com.rufat.onlineshopping.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
	private String username;
	private String email;
	private String password;
	private Role role;
}