package com.rufat.onlineshopping.service;

import com.rufat.onlineshopping.dto.AuthResponse;
import com.rufat.onlineshopping.dto.LoginRequest;
import com.rufat.onlineshopping.dto.RegisterRequest;
import com.rufat.onlineshopping.entity.Role;
import com.rufat.onlineshopping.entity.User;
import com.rufat.onlineshopping.repository.UserRepository;
import com.rufat.onlineshopping.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtUtils jwtUtils;

	@InjectMocks
	private AuthService authService;

	private RegisterRequest registerRequest;
	private LoginRequest loginRequest;

	@BeforeEach
	void setUp() {
		registerRequest = new RegisterRequest();
		registerRequest.setUsername("rufat");
		registerRequest.setEmail("rufat@gmail.com");
		registerRequest.setPassword("password123");

		loginRequest = new LoginRequest();
		loginRequest.setUsername("rufat");
		loginRequest.setPassword("password123");
	}

	@Test
	void register_WhenValidRequest_ShouldRegisterUserSuccessfully() {
		// Arrange
		when(userRepository.existsByUsername("rufat")).thenReturn(false);
		when(userRepository.existsByEmail("rufat@gmail.com")).thenReturn(false);
		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

		// Act
		String result = authService.register(registerRequest);

		// Assert
		assertEquals("İstifadəçi uğurla qeydiyyatdan keçdi!", result);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository, times(1)).save(userCaptor.capture());
		assertEquals(Role.CUSTOMER, userCaptor.getValue().getRole());
	}

	@Test
	void register_WhenUsernameAlreadyExists_ShouldThrowException() {
		// Arrange
		when(userRepository.existsByUsername("rufat")).thenReturn(true);

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			authService.register(registerRequest);
		});

		assertEquals("Xəta: Bu istifadəçi adı artıq götürülüb!", exception.getMessage());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void register_WhenEmailAlreadyExists_ShouldThrowException() {
		// Arrange
		when(userRepository.existsByUsername("rufat")).thenReturn(false);
		when(userRepository.existsByEmail("rufat@gmail.com")).thenReturn(true);

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			authService.register(registerRequest);
		});

		assertEquals("Xəta: Bu email artıq istifadə olunub!", exception.getMessage());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void login_WhenCredentialsAreValid_ShouldReturnAuthResponse() {
		// Arrange
		Authentication authentication = mock(Authentication.class);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);
		when(jwtUtils.generateJwtToken(authentication)).thenReturn("mocked-jwt-token");

		// Act
		AuthResponse response = authService.login(loginRequest);

		// Assert
		assertNotNull(response);
		assertEquals("mocked-jwt-token", response.getToken());
		assertEquals("Daxil olundu", response.getMessage());
		verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(jwtUtils, times(1)).generateJwtToken(authentication);
	}
}
