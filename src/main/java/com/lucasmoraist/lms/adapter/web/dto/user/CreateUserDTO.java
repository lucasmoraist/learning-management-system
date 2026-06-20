package com.lucasmoraist.lms.adapter.web.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateUserDTO(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 255, message = "Name must be between 3 and 100 characters")
        String name,
        @NotNull(message = "Birth date is required")
        LocalDate birthDate,
        @Size(min = 11, max = 14, message = "Document must be between 11 and 14 characters")
        @NotBlank(message = "Document is required")
        String document,
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 255, message = "Password must be at least 6 characters long")
        String password
) {

}
