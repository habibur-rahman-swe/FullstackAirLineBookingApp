package com.hr.airline.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hr.airline.dtos.Response;
import com.hr.airline.dtos.RoleDTO;
import com.hr.airline.entities.Role;
import com.hr.airline.exceptions.NotFoundException;
import com.hr.airline.ripo.RoleRepo;
import com.hr.airline.services.RoleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

	public final RoleRepo roleRepo;
	
	public final ModelMapper modelMapper;

	@Override
	public Response<?> createRole(RoleDTO roleDTO) {
		log.info("Inside createRole() with RoleDTO: {}", roleDTO);
		
		Role role = modelMapper.map(roleDTO, Role.class);
		role.setName(role.getName().toUpperCase());
		roleRepo.save(role);
		
		return Response.builder()
				.statusCode(HttpStatus.CREATED.value())
				.message("Role created successfully") 
				.build();
	}

	@Override
	public Response<?> updateRole(RoleDTO roleDTO) {
		log.info("Inside updateRole() with RoleDTO: {}", roleDTO);
		
		Long id = roleDTO.getId();
		
		Role existingRole = roleRepo.findById(id).orElseThrow(() -> new NotFoundException("Role not found"));
		existingRole.setName(roleDTO.getName().toUpperCase());
		roleRepo.save(existingRole);
		
		return Response.builder()
				.statusCode(HttpStatus.OK.value())
				.message("Role updated successfully")
				.build();
	}

	@Override
	public Response<List<RoleDTO>> getAllRoles() {
		log.info("Inside getAllRoles()");
		
		List<RoleDTO> roles = roleRepo.findAll().stream().map(role -> modelMapper.map(role, RoleDTO.class)).toList();
		
		return Response.<List<RoleDTO>>builder()
				.statusCode(HttpStatus.OK.value())
				.message(roles.isEmpty() ? "No roles found" : "Roles retrieved successfully")
				.data(roles)
				.build();
	}

}
