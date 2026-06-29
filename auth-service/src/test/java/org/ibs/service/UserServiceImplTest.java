package org.ibs.service;

import org.ibs.entity.Role;
import org.ibs.entity.User;
import org.ibs.exception.RoleNotFoundException;
import org.ibs.exception.UserNotFoundException;
import org.ibs.repository.RoleRepository;
import org.ibs.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        role = new Role();
        role.setId(1L);
        role.setRoleName("ADMIN");
    }

    @Test
    void updateUserRole_ShouldUpdateRoleSuccessfully() {
        // Arrange
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(roleRepository.findByRoleName("ADMIN"))
                .thenReturn(Optional.of(role));

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        // Act
        User updatedUser = userService.updateUserRole(1L, "ADMIN");

        // Assert
        assertNotNull(updatedUser);
        assertEquals(Set.of(role), updatedUser.getRoles());

        verify(userRepository).findById(1L);
        verify(roleRepository).findByRoleName("ADMIN");
        verify(userRepository).save(user);
    }

    @Test
    void updateUserRole_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUserRole(1L, "ADMIN")
        );

        assertEquals("User not found with id: 1", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(roleRepository, never()).findByRoleName(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserRole_ShouldThrowRoleNotFoundException_WhenRoleDoesNotExist() {
        // Arrange
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(roleRepository.findByRoleName("ADMIN"))
                .thenReturn(Optional.empty());

        // Act & Assert
        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> userService.updateUserRole(1L, "ADMIN")
        );

        assertEquals("Role not found: ADMIN", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(roleRepository).findByRoleName("ADMIN");
        verify(userRepository, never()).save(any());
    }
}
