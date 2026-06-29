package org.ibs.service;

import lombok.RequiredArgsConstructor;
import org.ibs.entity.Role;
import org.ibs.entity.User;
import org.ibs.exception.RoleNotFoundException;
import org.ibs.exception.UserNotFoundException;
import org.ibs.repository.RoleRepository;
import org.ibs.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl  implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Override
    public User updateUserRole(Long userId, String roleName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->  new UserNotFoundException("User not found with id: " + userId));
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return user;
    }
}