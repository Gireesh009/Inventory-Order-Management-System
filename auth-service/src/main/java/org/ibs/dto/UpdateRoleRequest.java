package org.ibs.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleRequest {
    private Long userId;
    private String role;
}