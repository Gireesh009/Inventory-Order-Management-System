package org.ibs.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    private UserDetails buildUser() {
        return new org.springframework.security.core.userdetails.User(
                "john",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void shouldGenerateAndParseTokenSuccessfully() {

        UserDetails user = buildUser();

        String token = jwtService.generateToken(user);

        assertNotNull(token);

        String username = jwtService.extractUsername(token);

        assertEquals("john", username);
    }

    @Test
    void shouldValidateTokenSuccessfully() {

        UserDetails user = buildUser();

        String token = jwtService.generateToken(user);

        boolean result = jwtService.isTokenValid(token, user);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForWrongUser() {

        UserDetails user = buildUser();
        String token = jwtService.generateToken(user);

        UserDetails otherUser = new org.springframework.security.core.userdetails.User(
                "alice",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }
}