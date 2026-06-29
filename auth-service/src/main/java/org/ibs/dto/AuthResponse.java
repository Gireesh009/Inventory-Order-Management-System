package org.ibs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private Long id;
    private String token;
    private String username;
    private String role;

    public AuthResponse(String userAlreadyExists, Object o) {
    }
}
