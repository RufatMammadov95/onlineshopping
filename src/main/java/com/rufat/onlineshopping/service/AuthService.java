package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.AuthResponse;
import com.rufat.onlineshopping.dto.LoginRequest;
import com.rufat.onlineshopping.dto.RegisterRequest;
import com.rufat.onlineshopping.entity.Role;
import com.rufat.onlineshopping.entity.User;
import com.rufat.onlineshopping.repository.UserRepository;
import com.rufat.onlineshopping.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
	}

	public String register(RegisterRequest request) {
		return register(request, Role.CUSTOMER);
	}

	public String registerAdmin(RegisterRequest request) {
		return register(request, Role.ADMIN);
	}

	private String register(RegisterRequest request, Role role) {
		boolean usernameExists = userRepository.existsByUsername(request.getUsername());
		boolean emailExists = userRepository.existsByEmail(request.getEmail());
		if (usernameExists && emailExists) {
			throw new RuntimeException("Xəta: Bu istifadəçi adı və email artıq qeydiyyatdan keçib!");
		}
		if (usernameExists) {
			throw new RuntimeException("Xəta: Bu istifadəçi adı artıq götürülüb!");
		}

		if (emailExists) {
			throw new RuntimeException("Xəta: Bu email artıq istifadə olunub!");
		}

		User user = User.builder().username(request.getUsername()).email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword())).role(role).build();

		userRepository.save(user);

		return "İstifadəçi uğurla qeydiyyatdan keçdi!";
	}

	public AuthResponse login(LoginRequest request) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwtToken = jwtUtils.generateJwtToken(authentication);

		return new AuthResponse(jwtToken, "Daxil olundu");
	}
}
