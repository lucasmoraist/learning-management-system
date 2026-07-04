package com.lucasmoraist.lms.adapter.web.dto.course;

import java.math.BigDecimal;
import java.util.UUID;

public record CourseProgressDTO(
        UUID courseId,
        UUID profileId,
        long totalLessons,
        long completedLessons,
        BigDecimal percentage
) {
}
