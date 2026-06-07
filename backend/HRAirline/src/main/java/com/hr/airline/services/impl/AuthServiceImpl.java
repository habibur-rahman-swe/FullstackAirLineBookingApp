package com.hr.airline.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hr.airline.dtos.LoginRequest;
import com.hr.airline.dtos.LoginResponse;
import com.hr.airline.dtos.RegistrationRequest;
import com.hr.airline.dtos.Response;
import com.hr.airline.entities.Role;
import com.hr.airline.entities.User;
import com.hr.airline.enums.AuthMethod;
import com.hr.airline.exceptions.BadRequestException;
import com.hr.airline.exceptions.NotFoundException;
import com.hr.airline.ripo.RoleRepo;
import com.hr.airline.ripo.UserRepo;
import com.hr.airline.security.JwtUtils;
import com.hr.airline.services.AuthService;
import com.hr.airline.services.EmailNotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;
	private final RoleRepo roleRepo;
	private final EmailNotificationService emailNotificationService;
	
	@Override
	public Response<?> register(RegistrationRequest registrationRequest) {
		log.info("inside register()");
		
		if (userRepo.existsByEmail(registrationRequest.getEmail())) {
			throw new BadRequestException("Email already exist");
		}
		
		List<Role> userRoles;
		
		if (registrationRequest.getRoles() != null && !registrationRequest.getRoles().isEmpty()) {
			userRoles = registrationRequest.getRoles().stream()
					.map(roleName -> roleRepo.findByName(roleName.toUpperCase())
							.orElseThrow(() -> new NotFoundException("Role: " + roleName + " not found")))
					.toList();
		} else {
			Role defaultRole = roleRepo.findByName("CUSTOMER")
					.orElseThrow(() -> new NotFoundException("Role customer doesn't exists"));
			userRoles = List.of(defaultRole);
		}
		
		User userToSave = new User();
		userToSave.setName(registrationRequest.getName());
		userToSave.setEmail(registrationRequest.getEmail());
		userToSave.setPhoneNumber(registrationRequest.getPhoneNumber());
		userToSave.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
		userToSave.setRoles(userRoles);
		userToSave.setCreatedAt(LocalDateTime.now());
		userToSave.setUpdatedAt(LocalDateTime.now());
		userToSave.setProvider(AuthMethod.LOCAL);
		userToSave.setActive(true);
		
		User savedUser = userRepo.save(userToSave);
		
		emailNotificationService.sendWelcomeEmail(savedUser);
		
		return Response.builder()
				.statusCode(HttpStatus.OK.value())
				.message("user registered successfully")
				.build();
		
	}

	@Override
	public Response<LoginResponse> login(LoginRequest loginRequest) {
		log.info("inside login()");
		User user = userRepo.findByEmail(loginRequest.getEmail())
				.orElseThrow(() -> new NotFoundException("User not found with email : " + loginRequest.getEmail()));
		
		if (!user.isActive()) {
			throw new NotFoundException("Account not active. please reach out to the customer care..");
		}
		
		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new BadRequestException("invalid password");
		}
		
		String token = jwtUtils.generatedToken(user.getEmail());
		
		List<String> roleNames = user.getRoles().stream()
				.map(Role::getName)
				.toList();
		
		LoginResponse loginResponse = new LoginResponse();
		loginResponse.setToken(token);
		loginResponse.setRoles(roleNames);
		
		return Response.<LoginResponse>builder()
				.statusCode(HttpStatus.OK.value())
				.message("login successfully")
				.data(loginResponse)
				.build();
	}

}
