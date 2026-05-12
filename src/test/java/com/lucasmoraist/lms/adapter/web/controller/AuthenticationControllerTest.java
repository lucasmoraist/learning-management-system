package com.lucasmoraist.lms.adapter.web.controller;

import com.lucasmoraist.lms.application.usecases.authentication.GenerateTokenCase;
import com.lucasmoraist.lms.domain.exceptions.AuthenticationException;
import com.lucasmoraist.lms.domain.exceptions.CertificateException;
import com.lucasmoraist.lms.domain.exceptions.TokenException;
import com.lucasmoraist.lms.domain.gateway.TokenGateway;
import com.lucasmoraist.lms.domain.model.auth.Token;
import com.lucasmoraist.lms.infrastructure.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    GenerateTokenCase generateTokenCase;
    @MockitoBean
    TokenGateway tokenGateway;
    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Should authenticate user and return token")
    void case01() throws Exception {
        final String dtoJson = """
                {
                  "email": "johndoe@email.com",
                  "password": "password123"
                }
                """;
        final Token validToken = new Token("token-valid", 3600);

        when(generateTokenCase.execute(any(), any())).thenReturn(validToken);

        mockMvc.perform(post("/api/v1/auth")
                        .contentType("application/json")
                        .with(jwt())
                        .content(dtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("accessToken").isNotEmpty())
                .andExpect(jsonPath("expiresIn").value(3600))
                .andExpect(jsonPath("type").value("Bearer"));

        verify(generateTokenCase, times(1)).execute(any(), any());
    }

    @Test
    @DisplayName("Should return 401 when authentication fails")
    void case02() throws Exception {
        final String dtoJson = """
                {
                  "email": "johndoe@email.com",
                  "password": "password123"
                }
                """;

        when(generateTokenCase.execute(any(), any())).thenThrow(new AuthenticationException("Invalid email or password"));

        mockMvc.perform(post("/api/v1/auth")
                        .contentType("application/json")
                        .with(jwt())
                        .content(dtoJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").value("Invalid email or password"));

        verify(generateTokenCase, times(1)).execute(any(), any());
    }

    @Test
    @DisplayName("Should return 503 when certificate error occurs")
    void case03() throws Exception {
        final String dtoJson = """
                {
                  "email": "johndoe@email.com",
                  "password": "password123"
                }
                """;

        when(generateTokenCase.execute(any(), any())).thenThrow(new CertificateException("Certificate error",
                new RuntimeException("Certificate error")));

        mockMvc.perform(post("/api/v1/auth")
                        .contentType("application/json")
                        .with(jwt())
                        .content(dtoJson))
                .andExpect(status().is(503));

        verify(generateTokenCase, times(1)).execute(any(), any());
    }

    @Test
    @DisplayName("Should return 401 when certificate error occurs")
    void case04() throws Exception {
        final String dtoJson = """
                {
                  "email": "johndoe@email.com",
                  "password": "password123"
                }
                """;

        when(generateTokenCase.execute(any(), any())).thenThrow(new TokenException("Token error",
                new RuntimeException("Token error")));

        mockMvc.perform(post("/api/v1/auth")
                        .contentType("application/json")
                        .with(jwt())
                        .content(dtoJson))
                .andExpect(status().isUnauthorized());

        verify(generateTokenCase, times(1)).execute(any(), any());
    }

    @Test
    @DisplayName("Should return 500 when unexpected error occurs")
    void case05() throws Exception {
        final String dtoJson = """
                {
                  "email": "johndoe@email.com",
                  "password": "password123"
                }
                """;

        when(generateTokenCase.execute(any(), any())).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/v1/auth")
                        .contentType("application/json")
                        .with(jwt())
                        .content(dtoJson))
                .andExpect(status().isInternalServerError());

        verify(generateTokenCase, times(1)).execute(any(), any());
    }

}