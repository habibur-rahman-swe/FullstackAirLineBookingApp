package com.hr.airline.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hr.airline.dtos.Response;
import com.hr.airline.dtos.UserDTO;
import com.hr.airline.entities.User;
import com.hr.airline.exceptions.NotFoundException;
import com.hr.airline.ripo.UserRepo;
import com.hr.airline.services.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;
	
	@Override
	public User currentUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
	}

	@Override
	@Transactional
	public Response<?> updateMyAccount(UserDTO userDTO) {
		log.info("inside updateMyAccount()");
		User user = currentUser();
		
		if (userDTO.getName() != null && !userDTO.getName().isBlank()) {
			user.setName(user.getName());
		}
		
		if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().isBlank()) {
			user.setPhoneNumber(userDTO.getPhoneNumber());
		}
		
		if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		}
		
		user.setUpdatedAt(LocalDateTime.now());
		
		userRepo.save(user);
		
		return Response.builder()
				.statusCode(HttpStatus.OK.value())
				.message("Account updated successfully")
				.build();
	}

	@Override
	public Response<List<UserDTO>> getAllPilots() {
		log.info("Inside getAllPilots()");
		
		List<UserDTO> pilots = userRepo.findByRoleName("PILOT").stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();
		
		return Response.<List<UserDTO>>builder()
				.statusCode(HttpStatus.OK.value())
				.message("Pilots retrieved successfully")
				.data(pilots)
				.build();
	}

	@Override
	public Response<UserDTO> getAccountDetails() {
		log.info("Inside getAccountDetails()");
		
		User user = currentUser();
		
		UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		
		return Response.<UserDTO>builder()
				.statusCode(HttpStatus.OK.value())
				.message("success")
				.data(userDTO)
				.build();
	}

}
