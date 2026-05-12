package com.lucasmoraist.lms.adapter.web.controller;

import com.lucasmoraist.lms.adapter.web.dto.user.CreateUserDTO;
import com.lucasmoraist.lms.adapter.web.dto.user.UpdateUserDTO;
import com.lucasmoraist.lms.application.usecases.user.CreateUserCase;
import com.lucasmoraist.lms.application.usecases.user.DeleteUserCase;
import com.lucasmoraist.lms.application.usecases.user.GetCurrentUserCase;
import com.lucasmoraist.lms.application.usecases.user.UpdateUserCase;
import com.lucasmoraist.lms.domain.enums.RoleType;
import com.lucasmoraist.lms.domain.exceptions.AuthenticationException;
import com.lucasmoraist.lms.domain.exceptions.UniqueKeyDatabaseException;
import com.lucasmoraist.lms.domain.gateway.TokenGateway;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.domain.model.user.Profile;
import com.lucasmoraist.lms.domain.model.user.Role;
import com.lucasmoraist.lms.infrastructure.security.service.CustomUserDetailsService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
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

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CreateUserCase createUserCase;
    @MockitoBean
    GetCurrentUserCase getCurrentUserCase;
    @MockitoBean
    UpdateUserCase updateUserCase;
    @MockitoBean
    DeleteUserCase deleteUserCase;
    @MockitoBean
    TokenGateway tokenGateway;
    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    // ---------------------------------
    // Tests for create user endpoint
    // ---------------------------------

    @Nested
    class CreateUserTest {

        @Test
        @DisplayName("Should create a new user and return 201 Created")
        void case01() throws Exception {
            String dtoJson = """
                    {
                        "name": "John Doe",
                        "birthDate": "2000-01-01",
                        "document": "86100550865",
                        "email": "johndoe@email.com",
                        "password": "password123",
                        "role": "STUDENT"
                    }
                    """;

            mockMvc.perform(post("/api/v1/users/register")
                            .contentType("application/json")
                            .with(jwt())
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
                            .with(jwt())
                            .content(invalidPayload))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 Conflict when email is already in use")
        void case03() throws Exception {
            String dtoJson = """
                    {
                        "name": "John Doe",
                        "birthDate": "2000-01-01",
                        "document": "86100550865",
                        "email": "johndoe@email.com",
                        "password": "password123",
                        "role": "STUDENT"
                    }
                    """;

            doThrow(UniqueKeyDatabaseException.class)
                    .when(createUserCase)
                    .execute(any(), any());

            mockMvc.perform(post("/api/v1/users/register")
                            .contentType("application/json")
                            .with(jwt())
                            .content(dtoJson))
                    .andExpect(status().isConflict());

            verify(createUserCase, times(1)).execute(anyString(), any(CreateUserDTO.class));
        }

    }

    // ---------------------------------
    // Tests for get current user endpoint
    // ---------------------------------

    @Nested
    class GetCurrentUserTest {

        static Identity identity;

        @BeforeAll
        static void setUp() {
            identity = Identity.builder()
                    .email("johndoe@email.com")
                    .password("password123")
                    .isActive(false)
                    .roles(Set.of(Role.builder()
                            .name(RoleType.INSTRUCTOR)
                            .build()))
                    .profile(Profile.builder()
                            .name("John Doe")
                            .birthDate(LocalDate.of(2000, 1, 1))
                            .build())
                    .build();
        }

        @Test
        @DisplayName("Should return 200 OK and current user")
        void case01() throws Exception {
            when(getCurrentUserCase.execute(anyString(), anyString())).thenReturn(identity);

            mockMvc.perform(get("/api/v1/users/me")
                            .contentType("application/json")
                            .header("Authorization", "Bearer valid-token")
                            .with(jwt()))
                    .andExpect(status().isOk());

            verify(getCurrentUserCase, times(1)).execute(anyString(), anyString());
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when token is missing")
        void case02() throws Exception {
            mockMvc.perform(get("/api/v1/users/me")
                            .contentType("application/json"))
                    .andExpect(status().isUnauthorized());

            verify(getCurrentUserCase, never()).execute(anyString(), anyString());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when header Authorization is missing")
        void case03() throws Exception {
            mockMvc.perform(get("/api/v1/users/me")
                            .contentType("application/json")
                            .with(jwt()))
                    .andExpect(status().isBadRequest());

            verify(getCurrentUserCase, never()).execute(anyString(), anyString());
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when token is invalid")
        void case04() throws Exception {
            when(getCurrentUserCase.execute(anyString(), anyString())).thenThrow(new AuthenticationException("Invalid token"));

            mockMvc.perform(get("/api/v1/users/me")
                            .contentType("application/json")
                            .header("Authorization", "Bearer invalid-token")
                            .with(jwt()))
                    .andExpect(status().isUnauthorized());

            verify(getCurrentUserCase, times(1)).execute(anyString(), anyString());
        }

    }

    // ---------------------------------
    // Tests for update user endpoint
    // ---------------------------------

    @Nested
    class UpdateCurrentUserTest {

        static Identity identity;

        @BeforeAll
        static void setUp() {
            identity = Identity.builder()
                    .email("johndoe@email.com")
                    .password("password123")
                    .isActive(false)
                    .roles(Set.of(Role.builder()
                            .name(RoleType.INSTRUCTOR)
                            .build()))
                    .profile(Profile.builder()
                            .name("John Doe")
                            .birthDate(LocalDate.of(2000, 1, 1))
                            .build())
                    .build();
        }

        @ParameterizedTest
        @MethodSource("validUpdatePayloads")
        @DisplayName("Should return 200 OK when update payload is valid")
        void case01(String validPayload) throws Exception {
            when(updateUserCase.execute(anyString(), anyString(), any(UpdateUserDTO.class)))
                    .thenReturn(identity);

            mockMvc.perform(patch("/api/v1/users/me/update")
                    .contentType("application/json")
                    .header("Authorization", "Bearer valid-token")
                    .content(validPayload)
                    .with(jwt()))
            .andExpect(status().isOk());

            verify(updateUserCase, times(1)).execute(anyString(), anyString(), any(UpdateUserDTO.class));
        }

        static Stream<Arguments> validUpdatePayloads() {
            return Stream.of(
                    Arguments.of("{\"profile\": {\"name\": \"John Doe\"}}"),
                    Arguments.of("{\"email\": \"novo@email.com\"}"),
                    Arguments.of("{\"profile\": {\"bio\": \"Java Developer\"}}"),
                    Arguments.of("{}"),
                    Arguments.of("""
                        {
                            "email": "johndoe@email.com",
                            "profile": {
                                "name": "John Doe",
                                "bio": "Engenheiro de Software Pleno"
                            }
                        }
                    """)
            );
        }

        @ParameterizedTest
        @MethodSource("invalidUpdatePayloads")
        @DisplayName("Should return 400 Bad Request when update payload is invalid")
        void case02(String invalidPayload) throws Exception {
            mockMvc.perform(patch("/api/v1/users/me/update")
                    .contentType("application/json")
                    .header("Authorization", "Bearer valid-token")
                    .content(invalidPayload)
                    .with(jwt()))
            .andExpect(status().isBadRequest());

            verify(updateUserCase, never()).execute(anyString(), anyString(), any(UpdateUserDTO.class));
        }

        static Stream<Arguments> invalidUpdatePayloads() {
            return Stream.of(
                    Arguments.of("{\"email\": \"email-invalido\"}"),
                    Arguments.of("{\"password\": \"12345\"}"),
                    Arguments.of("{\"profile\": {\"name\": \"J\"}}")
            );
        }

        @Test
        @DisplayName("Should return 403 Forbidden when token is missing")
        void case03() throws Exception {
            mockMvc.perform(patch("/api/v1/users/me/update")
                            .contentType("application/json")
                            .content("{}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when Authorization header is missing")
        void case04() throws Exception {
            mockMvc.perform(patch("/api/v1/users/me/update")
                            .contentType("application/json")
                            .content("{}")
                            .with(jwt()))
                    .andExpect(status().isBadRequest());
        }
    }

    // ---------------------------------
    // Tests for delete user endpoint
    // ---------------------------------

    @Nested
    class DeleteCurrentUserTest {

        @Test
        @DisplayName("Should return 204 No Content and delete current user")
        void case01() throws Exception {
            mockMvc.perform(delete("/api/v1/users/me/delete")
                            .contentType("application/json")
                            .header("Authorization", "Bearer valid-token")
                            .with(jwt()))
                    .andExpect(status().isNoContent());

            verify(deleteUserCase, times(1)).execute(anyString(), anyString());
        }

        @Test
        @DisplayName("Should return 403 Forbidden when token is missing")
        void case02() throws Exception {
            mockMvc.perform(delete("/api/v1/users/me/delete")
                            .contentType("application/json"))
                    .andExpect(status().isForbidden());

            verify(deleteUserCase, never()).execute(anyString(), anyString());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when header Authorization is missing")
        void case03() throws Exception {
            mockMvc.perform(delete("/api/v1/users/me/delete")
                            .contentType("application/json")
                            .with(jwt()))
                    .andExpect(status().isBadRequest());

            verify(deleteUserCase, never()).execute(anyString(), anyString());
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when token is invalid")
        void case04() throws Exception {
            doThrow(AuthenticationException.class)
                    .when(deleteUserCase)
                    .execute(anyString(), anyString());

            mockMvc.perform(delete("/api/v1/users/me/delete")
                            .contentType("application/json")
                            .header("Authorization", "Bearer invalid-token")
                            .with(jwt()))
                    .andExpect(status().isUnauthorized());

            verify(deleteUserCase, times(1)).execute(anyString(), anyString());
        }

        @Test
        @DisplayName("Should return 404 Not Found when user does not exist")
        void case05() throws Exception {
            doThrow(EntityNotFoundException.class)
                    .when(deleteUserCase)
                    .execute(anyString(), anyString());

            mockMvc.perform(delete("/api/v1/users/me/delete")
                            .contentType("application/json")
                            .header("Authorization", "Bearer valid-token")
                            .with(jwt()))
                    .andExpect(status().isNotFound());

            verify(deleteUserCase, times(1)).execute(anyString(), anyString());
        }

    }

}