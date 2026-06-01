package com.hr.airline.exceptions;

public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BadRequestException(String ex){
        super(ex);
    }
}
