package org.ibs.config;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class JwtAuthenticationTest {

    private final JwtAuthentication jwtAuthentication = new JwtAuthentication();

    @Test
    void shouldReturnUnauthorizedResponse() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) {
                outputStream.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }
        };

        when(response.getOutputStream()).thenReturn(servletOutputStream);

        AuthenticationException exception =
                new AuthenticationException("Invalid token") {};

        jwtAuthentication.commence(request, response, exception);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");

        String jsonResponse = outputStream.toString();

        assertTrue(jsonResponse.contains("\"status\":401"));
        assertTrue(jsonResponse.contains("Invalid or missing token"));
    }
}