package com.hr.airline.security;

import java.util.Collection;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hr.airline.entities.User;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthUser implements UserDetails {

	private static final long serialVersionUID = 1L;

	private User user;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
	}

	@Override
	public @Nullable String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}
	
	@Override
	public boolean isEnabled() {
		return user.isActive();
	}

}
