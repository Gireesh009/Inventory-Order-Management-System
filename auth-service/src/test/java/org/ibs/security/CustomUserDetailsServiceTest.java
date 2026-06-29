package org.ibs.security;

import org.ibs.entity.User;
import org.ibs.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void shouldReturnUserDetailsWhenUserExists() {

        User user = new User();
        user.setUsername("john");
        user.setPassword("pass");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        var result = service.loadUserByUsername("john");

        assertEquals("john", result.getUsername());
        assertEquals("pass", result.getPassword());

        verify(userRepository).findByUsername("john");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("john")
        );

        verify(userRepository).findByUsername("john");
    }
}