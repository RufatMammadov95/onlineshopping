package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.UserProfileDto;
import com.rufat.onlineshopping.entity.User;
import com.rufat.onlineshopping.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional(readOnly = true)
	public UserProfileDto getProfile(String username) {
		return toDto(userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found")));
	}

	@Transactional
	public UserProfileDto updateProfile(String currentUsername, UserProfileDto request) {
		User user = userRepository.findByUsername(currentUsername)
				.orElseThrow(() -> new RuntimeException("User not found"));
		if (!user.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername()))
			throw new RuntimeException("Username is already in use");
		if (!user.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.existsByEmail(request.getEmail()))
			throw new RuntimeException("Email is already in use");
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		if (request.getPassword() != null && !request.getPassword().isBlank())
			user.setPassword(passwordEncoder.encode(request.getPassword()));
		return toDto(userRepository.save(user));
	}

	private UserProfileDto toDto(User user) {
		UserProfileDto dto = new UserProfileDto();
		dto.setUsername(user.getUsername());
		dto.setEmail(user.getEmail());
		return dto;
	}
}
