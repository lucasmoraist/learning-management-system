package com.lucasmoraist.lms.application.usecases.course;

import com.lucasmoraist.lms.adapter.web.dto.course.CourseProgressDTO;
import com.lucasmoraist.lms.application.usecases.user.GetCurrentUserCase;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.infrastructure.database.persistence.CoursePersistence;
import com.lucasmoraist.lms.infrastructure.database.persistence.LessonProgressPersistence;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCourseProgressCase {

    private static final int PERCENTAGE_SCALE = 2;

    private final CoursePersistence coursePersistence;
    private final LessonProgressPersistence lessonProgressPersistence;
    private final GetCurrentUserCase getCurrentUserCase;

    public CourseProgressDTO execute(String traceId, UUID courseId, String authorization) {
        Identity currentUser = getCurrentUserCase.execute(traceId, authorization);
        UUID profileId = currentUser.getProfile().getId();

        log.debug("[{}] - Calculating course progress for course {} and profile {}", traceId, courseId, profileId);

        if (!coursePersistence.existsById(courseId)) {
            log.error("[{}] - Course with ID {} not found", traceId, courseId);
            throw new EntityNotFoundException("Course not found");
        }

        long totalLessons = lessonProgressPersistence.countLessonsByCourseId(courseId);
        long completedLessons = lessonProgressPersistence.countCompletedLessonsByProfileAndCourse(profileId, courseId);
        BigDecimal percentage = calculatePercentage(completedLessons, totalLessons);

        return new CourseProgressDTO(courseId, profileId, totalLessons, completedLessons, percentage);
    }

    private BigDecimal calculatePercentage(long completedLessons, long totalLessons) {
        if (totalLessons == 0) {
            return BigDecimal.ZERO.setScale(PERCENTAGE_SCALE, RoundingMode.HALF_UP);
        }

        return BigDecimal.valueOf(completedLessons)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalLessons), PERCENTAGE_SCALE, RoundingMode.HALF_UP);
    }

}
