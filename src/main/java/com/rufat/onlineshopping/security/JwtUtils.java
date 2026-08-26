package com.rufat.onlineshopping.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

	@Value("${app.jwt.secret}")
	private String jwtSecret;

	@Value("${app.jwt.expiration-ms}")
	private int jwtExpirationMs;

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

	public String generateJwtToken(Authentication authentication) {
		UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
		String role = userPrincipal.getAuthorities().stream().findFirst().map(authority -> authority.getAuthority())
				.orElse("ROLE_CUSTOMER").replace("ROLE_", "");

		return Jwts.builder().setSubject(userPrincipal.getUsername()).setIssuedAt(new Date()).claim("role", role)
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	public String getUsernameFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
		}
		return false;
	}
}
