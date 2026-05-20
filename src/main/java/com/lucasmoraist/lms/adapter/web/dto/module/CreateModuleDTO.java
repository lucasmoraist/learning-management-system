package com.lucasmoraist.lms.adapter.web.dto.module;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateModuleDTO(
        @NotBlank(message = "Module title is required")
        @Size(max = 255, message = "Module title must be less than 255 characters")
        String title
) {

}
