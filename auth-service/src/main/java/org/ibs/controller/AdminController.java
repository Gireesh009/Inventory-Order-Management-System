package org.ibs.controller;

import lombok.RequiredArgsConstructor;
import org.ibs.dto.UpdateRoleRequest;
import org.ibs.dto.UpdateRoleResponse;
import org.ibs.entity.User;
import org.ibs.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;



    @PatchMapping("/users/role")
    public ResponseEntity<UpdateRoleResponse> updateUserRole(
            @RequestBody UpdateRoleRequest request) {

        User updatedUser = userService.updateUserRole(request.getUserId(), request.getRole());
        return ResponseEntity.ok(
                new UpdateRoleResponse(
                        updatedUser.getId(),
                        updatedUser.getUsername(),
                        request.getRole(),
                        "Role updated successfully"));
    }
}
