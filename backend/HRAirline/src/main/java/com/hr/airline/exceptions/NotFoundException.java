package com.hr.airline.exceptions;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotFoundException(String ex) {
		super(ex);
	}
}
