package com.rufat.onlineshopping.controller;

import com.rufat.onlineshopping.dto.RegisterRequest;
import com.rufat.onlineshopping.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	private final AuthService authService;

	public AdminController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/users")
	public ResponseEntity<String> createAdmin(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerAdmin(request));
	}
}
