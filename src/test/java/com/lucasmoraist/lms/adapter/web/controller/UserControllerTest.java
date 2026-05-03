package com.lucasmoraist.lms.adapter.web.controller;

import com.lucasmoraist.lms.adapter.web.dto.user.CreateUserDTO;
import com.lucasmoraist.lms.application.usecases.user.CreateUserCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    // ---------------------------------
    // Tests for create user endpoint
    // ---------------------------------

    @Nested
    class CreateUserTest {

        @Autowired
        MockMvc mockMvc;
        @MockitoBean
        CreateUserCase createUserCase;

        @Test
        @DisplayName("Should create a new user and return 201 Created")
        void case01() throws Exception {
            String dtoJson = """
                    {
                        "name": "John Doe",
                        "birthDate": "2000-01-01",
                        "email": "johndoe@email.com",
                        "password": "password123",
                        "role": "STUDENT"
                    }
                    """;

            mockMvc.perform(post("/api/v1/users/register")
                            .contentType("application/json")
                            .content(dtoJson))
                    .andExpect(status().isCreated())
                    .andExpect(header().stringValues("Location", "/api/v1/auth/login"));

            verify(createUserCase, times(1)).execute(anyString(), any(CreateUserDTO.class));
        }

        static Stream<Arguments> invalidPayloads() {
            return Stream.of(
                    Arguments.of("""
                    {
                        "name": "",
                        "birthDate": "2000-01-01",
                        "email": "johndoe@email.com",
                        "password": "password123",
                        "role": "STUDENT"
                    }
                    """),
                    Arguments.of("""
                    {
                        "name": "Jo",
                        "birthDate": "2000-01-01",
                        "email": "johndoe@email.com",
                        "password": "password123",
                        "role": "STUDENT"
                    }
                    """),
                    Arguments.of("""
                    {
                        "name": "John Doe",
                        "email": "johndoe@email.com",
                        "password": "password123",
                        "role": "STUDENT"
                    }
                    """),
                    Arguments.of("""
                    {
                        "name": "John Doe",
                        "birthDate": "2000-01-01",
                        "password": "password123",
                        "role": "STUDENT"
                    }
                    """),
                    Arguments.of("""
                    {
                        "name": "John Doe",
                        "email": "invalid-email",
                        "birthDate": "2000-01-01",
                        "password": "password123",
                        "role": "STUDENT"
                    }
                    """),
                    Arguments.of("""
                    {
                        "name": "John Doe",
                        "birthDate": "2000-01-01",
                        "email": "johndoe@email.com",
                        "password": "12345",
                        "role": "STUDENT"
                    }
                    """),
                    Arguments.of("""
                    {
                        "name": "John Doe",
                        "birthDate": "2000-01-01",
                        "email": "johndoe@email.com",
                        "role": "STUDENT"
                    }
                    """),
                    Arguments.of("""
                    {
                        "name": "John Doe",
                        "birthDate": "2000-01-01",
                        "email": "johndoe@email.com",
                        "password": "password123"
                    }
                    """)
            );
        }

        @ParameterizedTest
        @MethodSource("invalidPayloads")
        @DisplayName("Should return 400 Bad Request when payload is invalid")
        void case02(String invalidPayload) throws Exception {
            mockMvc.perform(post("/api/v1/users/register")
                            .contentType("application/json")
                            .content(invalidPayload))
                    .andExpect(status().isBadRequest());
        }

    }

}