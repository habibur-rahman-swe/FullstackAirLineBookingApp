package com.hr.airline.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr.airline.dtos.Response;
import com.hr.airline.dtos.RoleDTO;
import com.hr.airline.services.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

	private final RoleService roleService;

	@PostMapping
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Response<?>> createRole(@Valid @RequestBody RoleDTO roleDTO) {
		Response<?> response = roleService.createRole(roleDTO);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@PutMapping
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Response<?>> updateRole(@Valid @RequestBody RoleDTO roleDTO) {
		Response<?> response = roleService.updateRole(roleDTO);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}

	@GetMapping
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PILOT')")
	public ResponseEntity<Response<?>> getAllRoles() {
		Response<?> response = roleService.getAllRoles();
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
}
