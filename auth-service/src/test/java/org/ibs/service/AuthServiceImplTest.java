package org.ibs.service;

import org.ibs.dto.*;
import org.ibs.entity.Role;
import org.ibs.entity.User;
import org.ibs.exception.UserAlreadyExistsException;
import org.ibs.repository.RoleRepository;
import org.ibs.repository.UserRepository;
import org.ibs.security.CustomUserDetails;
import org.ibs.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_ShouldRegisterUserSuccessfully() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@test.com");
        request.setPassword("password");

        Role role = Role.builder()
                .roleName("ROLE_USER")
                .build();

        when(userRepository.existsByUsername("john"))
                .thenReturn(false);

        when(userRepository.existsByEmail("john@test.com"))
                .thenReturn(false);

        when(roleRepository.findByRoleName("ROLE_USER"))
                .thenReturn(Optional.of(role));

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        when(jwtService.generateToken(any(CustomUserDetails.class)))
                .thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("john", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
        assertEquals("jwt-token", response.getToken());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenUserExists() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@test.com");

        when(userRepository.existsByUsername("john"))
                .thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {

        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("password");

        Role role = Role.builder()
                .roleName("ROLE_USER")
                .build();

        User user = User.builder()
                .username("john")
                .roles(Set.of(role))
                .build();

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(any(CustomUserDetails.class)))
                .thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("john", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
        assertEquals("jwt-token", response.getToken());

        verify(authenticationManager)
                .authenticate(any());
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {

        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("password");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );
    }

    @Test
    void refreshToken_ShouldGenerateNewAccessToken() {

        RefreshTokenRequest request =
                new RefreshTokenRequest();

        request.setRefreshToken("refresh-token");

        UserDetails userDetails =
                mock(UserDetails.class);

        when(jwtService.extractUsername("refresh-token"))
                .thenReturn("john");

        when(userDetailsService.loadUserByUsername("john"))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(
                eq("refresh-token"),
                eq(userDetails)))
                .thenReturn(true);

        when(jwtService.generateToken(userDetails))
                .thenReturn("new-access-token");

        @SuppressWarnings({"rawtypes", "unchecked"})
        Collection authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        when(userDetails.getAuthorities())
                .thenReturn(authorities);

        AuthResponse response =
                authService.refreshToken(request);

        assertEquals("john", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
        assertEquals(
                "new-access-token",
                response.getToken()
        );
    }

    @Test
    void refreshToken_ShouldThrowException_WhenTokenInvalid() {

        RefreshTokenRequest request =
                new RefreshTokenRequest();

        request.setRefreshToken("invalid-token");

        UserDetails userDetails =
                mock(UserDetails.class);

        when(jwtService.extractUsername("invalid-token"))
                .thenReturn("john");

        when(userDetailsService.loadUserByUsername("john"))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(
                "invalid-token",
                userDetails))
                .thenReturn(false);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.refreshToken(request)
                );

        assertEquals(
                "Its valid token",
                exception.getMessage()
        );
    }

    @Test
    void updateUserRole_ShouldUpdateRoleSuccessfully() {

        UpdateRoleRequest request =
                new UpdateRoleRequest();

        request.setUserId(1L);
        request.setRole("ROLE_ADMIN");

        var response =
                authService.updateUserRole(request);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                "User role updated successfully",
                response.getBody()
        );

        verify(userService)
                .updateUserRole(1L, "ROLE_ADMIN");
    }
}
