package org.ibs.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ibs.dto.AuthResponse;
import org.ibs.dto.LoginRequest;
import org.ibs.dto.RefreshTokenRequest;
import org.ibs.dto.RegisterRequest;
import org.ibs.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     register new users
     **/
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    /**
     Login users
     **/
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
      Refresh JWT token when access token expires
     **/
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }


}