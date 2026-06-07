package com.hr.airline.services;

import com.hr.airline.dtos.LoginRequest;
import com.hr.airline.dtos.LoginResponse;
import com.hr.airline.dtos.RegistrationRequest;
import com.hr.airline.dtos.Response;

public interface AuthService {

	Response<?> register(RegistrationRequest registrationRequest);

	Response<LoginResponse> login(LoginRequest loginRequest);
}
