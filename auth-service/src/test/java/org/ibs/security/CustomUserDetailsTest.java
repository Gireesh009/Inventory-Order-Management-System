package org.ibs.security;

import org.ibs.entity.Role;
import org.ibs.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void shouldMapUserToAuthorities() {

        Role role = new Role();
        role.setRoleName("ROLE_ADMIN");

        User user = new User();
        user.setUsername("john");
        user.setPassword("pass");
        user.setEnabled(true);
        user.setRoles(Set.of(role));

        CustomUserDetails details = new CustomUserDetails(user);

        assertEquals("john", details.getUsername());
        assertEquals("pass", details.getPassword());
        assertTrue(details.isEnabled());

        assertEquals(1, details.getAuthorities().size());

        GrantedAuthority authority = details.getAuthorities()
                .iterator()
                .next();

        assertEquals("ROLE_ADMIN", authority.getAuthority());
    }

    @Test
    void shouldHandleNullRolesGracefully() {

        User user = new User();
        user.setUsername("john");
        user.setPassword("pass");
        user.setEnabled(true);
        user.setRoles(null); // important edge case

        CustomUserDetails details = new CustomUserDetails(user);

        assertNotNull(details.getAuthorities());
        assertTrue(details.getAuthorities().isEmpty());
    }

    @Test
    void shouldReturnAccountStatusCorrectly() {

        User user = new User();
        user.setUsername("john");
        user.setPassword("pass");
        user.setEnabled(true);
        user.setRoles(Set.of());

        CustomUserDetails details = new CustomUserDetails(user);

        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
        assertTrue(details.isEnabled());
    }
}