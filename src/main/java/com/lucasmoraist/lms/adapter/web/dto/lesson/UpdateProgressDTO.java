package com.lucasmoraist.lms.adapter.web.dto.lesson;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateProgressDTO(
        @NotNull @PositiveOrZero Integer currentSeconds
) {

}
