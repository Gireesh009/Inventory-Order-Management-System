package org.ibs.service;


import jakarta.validation.Valid;
import org.ibs.dto.AuthResponse;
import org.ibs.dto.LoginRequest;
import org.ibs.dto.RefreshTokenRequest;
import org.ibs.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(@Valid RefreshTokenRequest request);

}
