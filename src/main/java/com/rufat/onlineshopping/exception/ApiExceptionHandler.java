package com.rufat.onlineshopping.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		return response(HttpStatus.BAD_REQUEST, "Validation failed", errors);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, Object>> forbidden() {
		return response(HttpStatus.FORBIDDEN, "Access denied", null);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, Object>> business(RuntimeException ex) {
		return response(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> unexpected(Exception ex) {
		return response(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", null);
	}

	private ResponseEntity<Map<String, Object>> response(HttpStatus status, String message,
			Map<String, String> errors) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now());
		body.put("status", status.value());
		body.put("message", message);
		if (errors != null)
			body.put("errors", errors);
		return ResponseEntity.status(status).body(body);
	}
}
