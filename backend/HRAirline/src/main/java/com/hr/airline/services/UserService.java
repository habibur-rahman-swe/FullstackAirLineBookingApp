package com.hr.airline.services;

import java.util.List;

import com.hr.airline.dtos.Response;
import com.hr.airline.dtos.UserDTO;
import com.hr.airline.entities.User;

public interface UserService {
	User currentUser();

	Response<?> updateMyAccount(UserDTO userDTO);

	Response<List<UserDTO>> getAllPilots();

	Response<UserDTO> getAccountDetails();
}
