package org.ibs.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String username;

    @Email
    private String email;

    @NotBlank
    private String password;
}