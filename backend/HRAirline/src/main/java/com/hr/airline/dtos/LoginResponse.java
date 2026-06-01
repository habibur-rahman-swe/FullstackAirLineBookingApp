package com.hr.airline.dtos;

import java.util.List;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private List<String> roles;
}

