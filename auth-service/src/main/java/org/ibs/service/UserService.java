package org.ibs.service;

import org.ibs.entity.User;

public interface  UserService {
    User updateUserRole(Long userId, String role);
}
