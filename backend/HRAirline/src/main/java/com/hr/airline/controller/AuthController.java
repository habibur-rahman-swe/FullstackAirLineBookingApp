package com.hr.airline.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr.airline.dtos.LoginRequest;
import com.hr.airline.dtos.RegistrationRequest;
import com.hr.airline.dtos.Response;
import com.hr.airline.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<Response<?>> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
		return ResponseEntity.ok(authService.register(registrationRequest));
	}
	
	@PostMapping("/login")
	public ResponseEntity<Response<?>> login(@Valid @RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(authService.login(loginRequest));
	}

}
