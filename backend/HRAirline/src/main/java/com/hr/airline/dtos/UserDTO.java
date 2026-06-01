package com.hr.airline.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hr.airline.enums.AuthMethod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String name;

    private String email;

    private String phoneNumber;

    /// / Write-only: Included when receiving data, excluded when sending data
    /// //Only used for writing (deserialization), ignored during reading (serialization)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private boolean emailVerified;

    // For OAuth2 authentication
    private AuthMethod provider;
    private String providerId; // ID from OAuth provider

    private List<RoleDTO> roles;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
