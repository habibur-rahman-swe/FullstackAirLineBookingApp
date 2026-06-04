package com.hr.airline.services;

import java.util.List;

import com.hr.airline.dtos.Response;
import com.hr.airline.dtos.RoleDTO;

public interface RoleService {
	Response<?> createRole(RoleDTO roleDTO);

	Response<?> updateRole(RoleDTO roleDTO);

	Response<List<RoleDTO>> getAllRoles();
}
