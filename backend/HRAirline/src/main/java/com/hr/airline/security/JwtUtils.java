package com.hr.airline.security;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;

@Service
public class JwtUtils {

	@Value("${app.jwt.secret-key}")
	private String secretKey;
	
	@Value("${app.jwt.expiration-ms}")
	private Long expirationMs;
	
	private SecretKey key;
	
	@PostConstruct
	private void init() {
		byte[] keyBytes = secretKey.getBytes();
		this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
	}
	
	public String generatedToken(String email) {
		return Jwts.builder()
				.subject(email)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expirationMs))
				.signWith(key)
				.compact();
	}
	
	public String getUsernameFromToken(String token) {
		return extractClaims(token, Claims::getSubject);
	}

	private <T> T extractClaims(String token, Function<Claims, T> claimsFunction) {
		return claimsFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
	}
	
	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = getUsernameFromToken(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}
	
	private boolean isTokenExpired(String token) {
		Date expiration = extractClaims(token, Claims::getExpiration);
		return expiration.before(new Date());
	}
}
