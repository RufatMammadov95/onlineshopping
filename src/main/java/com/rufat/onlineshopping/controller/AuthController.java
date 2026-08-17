package com.rufat.onlineshopping.controller;

import com.rufat.onlineshopping.dto.AuthResponse;
import com.rufat.onlineshopping.dto.LoginRequest;
import com.rufat.onlineshopping.dto.RegisterRequest;
import com.rufat.onlineshopping.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}
}