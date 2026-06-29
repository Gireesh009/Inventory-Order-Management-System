package org.ibs.service;


import lombok.RequiredArgsConstructor;
import org.ibs.dto.*;
import org.ibs.entity.Role;
import org.ibs.entity.User;
import org.ibs.exception.UserAlreadyExistsException;
import org.ibs.repository.RoleRepository;
import org.ibs.repository.UserRepository;
import org.ibs.security.CustomUserDetails;
import org.ibs.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())
                || userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }


        Role role = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(role))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .enabled(true)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(new CustomUserDetails(user));

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .id(user.getId())
                .role("ROLE_USER")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        String token = jwtService.generateToken(new CustomUserDetails(user));

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRoles().iterator().next().getRoleName())
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Extract username from refresh token
        String username = jwtService.extractUsername(refreshToken);

        // Load user details
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(username);


        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new RuntimeException("Its valid token");
        }


        // Generate new access token
        String newAccessToken = jwtService.generateToken(userDetails);

        //Return response
        return AuthResponse.builder()
                .token(newAccessToken)

                .username(username)
                .role(userDetails.getAuthorities()
                        .iterator().next().getAuthority())
                .build();
    }



    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/role")
    public ResponseEntity<String> updateUserRole(
            @RequestBody UpdateRoleRequest request) {

        userService.updateUserRole(request.getUserId(), request.getRole());
        return ResponseEntity.ok("User role updated successfully");
    }
}

