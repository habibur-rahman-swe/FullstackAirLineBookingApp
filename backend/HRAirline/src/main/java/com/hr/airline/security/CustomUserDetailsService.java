package com.hr.airline.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hr.airline.entities.User;
import com.hr.airline.exceptions.NotFoundException;
import com.hr.airline.ripo.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	private final UserRepo userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByEmail(username)
				.orElseThrow(() -> new NotFoundException("User not found with email: " + username));
		
		return AuthUser.builder().user(user).build();
	}

}
