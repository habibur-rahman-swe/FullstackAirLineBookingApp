package com.hr.airline.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr.airline.dtos.Response;
import com.hr.airline.dtos.UserDTO;
import com.hr.airline.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PutMapping
	public ResponseEntity<Response<?>> updateMyAccount(@RequestBody UserDTO userDTO) {
		return ResponseEntity.ok(userService.updateMyAccount(userDTO));
	}

	@GetMapping("/pilots")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PILOT')")
	public ResponseEntity<Response<List<UserDTO>>> getAllPilots() {
		return ResponseEntity.ok(userService.getAllPilots());
	}

	@GetMapping("/me")
	public ResponseEntity<Response<UserDTO>> getAccountDetails() {
		return ResponseEntity.ok(userService.getAccountDetails());
	}
}
