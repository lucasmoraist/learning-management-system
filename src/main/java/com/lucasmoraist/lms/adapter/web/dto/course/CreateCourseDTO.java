package com.lucasmoraist.lms.adapter.web.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCourseDTO(
        @NotBlank(message = "Title is required")
        @Size(min = 5, max = 255)
        String title,
        @NotBlank(message = "Description is required")
        String description
) {

}
