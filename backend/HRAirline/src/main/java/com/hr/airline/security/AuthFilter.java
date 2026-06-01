package com.hr.airline.security;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hr.airline.exceptions.CustomAuthenticationEntryPoint;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;
	private final CustomUserDetailsService customUserDetailsService;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = getTokenFromRequest(request);

		if (token != null) {
			String email;

			try {
				email = jwtUtils.getUsernameFromToken(token);
			} catch (Exception ex) {
				AuthenticationException authenticationException = new BadCredentialsException(ex.getMessage());
				customAuthenticationEntryPoint.commence(request, response, authenticationException);
				return;
			}

			UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
			if (jwtUtils.isTokenValid(token, userDetails)) {
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}
		
		try {
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private String getTokenFromRequest(HttpServletRequest request) {
		String tokenWithBearer = request.getHeader("Authorization");

		if (tokenWithBearer != null && tokenWithBearer.startsWith("Bearer ")) {
			return tokenWithBearer.substring(7);
		}

		return null;
	}
}
