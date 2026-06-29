package org.ibs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ibs.dto.UpdateRoleRequest;
import org.ibs.entity.User;
import org.ibs.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void shouldUpdateUserRoleSuccessfully() throws Exception {

        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setUserId(1L);
        request.setRole("ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        when(userService.updateUserRole(1L, "ADMIN")).thenReturn(user);

        mockMvc.perform(patch("/admin/users/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.message").value("Role updated successfully"));

        verify(userService, times(1)).updateUserRole(1L, "ADMIN");
    }
}