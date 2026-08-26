package com.rufat.onlineshopping.controller;

import com.rufat.onlineshopping.dto.UserProfileDto;
import com.rufat.onlineshopping.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me")
public class UserController {
	private final UserService userService;
	public UserController(UserService userService) { this.userService = userService; }
	@GetMapping public ResponseEntity<UserProfileDto> getProfile(Authentication authentication) { return ResponseEntity.ok(userService.getProfile(authentication.getName())); }
	@PutMapping public ResponseEntity<UserProfileDto> updateProfile(Authentication authentication, @Valid @RequestBody UserProfileDto request) { return ResponseEntity.ok(userService.updateProfile(authentication.getName(), request)); }
}
