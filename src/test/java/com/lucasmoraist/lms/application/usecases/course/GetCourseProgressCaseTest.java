package com.lucasmoraist.lms.application.usecases.course;

import com.lucasmoraist.lms.adapter.web.dto.course.CourseProgressDTO;
import com.lucasmoraist.lms.application.usecases.user.GetCurrentUserCase;
import com.lucasmoraist.lms.application.utils.TraceIdUtils;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.domain.model.user.Profile;
import com.lucasmoraist.lms.infrastructure.database.persistence.CoursePersistence;
import com.lucasmoraist.lms.infrastructure.database.persistence.LessonProgressPersistence;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCourseProgressCaseTest {

    @InjectMocks
    GetCourseProgressCase getCourseProgressCase;
    @Mock
    CoursePersistence coursePersistence;
    @Mock
    LessonProgressPersistence lessonProgressPersistence;
    @Mock
    GetCurrentUserCase getCurrentUserCase;

    String traceId;
    String authorization;
    UUID courseId;
    UUID profileId;

    @BeforeEach
    void setUp() {
        traceId = TraceIdUtils.generateTraceId();
        authorization = "Bearer valid-token";
        courseId = UUID.randomUUID();
        profileId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should calculate course progress percentage")
    void case01() {
        when(getCurrentUserCase.execute(traceId, authorization)).thenReturn(currentUser());
        when(coursePersistence.existsById(courseId)).thenReturn(true);
        when(lessonProgressPersistence.countLessonsByCourseId(courseId)).thenReturn(4L);
        when(lessonProgressPersistence.countCompletedLessonsByProfileAndCourse(profileId, courseId)).thenReturn(3L);

        CourseProgressDTO progress = getCourseProgressCase.execute(traceId, courseId, authorization);

        assertEquals(courseId, progress.courseId());
        assertEquals(profileId, progress.profileId());
        assertEquals(4L, progress.totalLessons());
        assertEquals(3L, progress.completedLessons());
        assertEquals(new BigDecimal("75.00"), progress.percentage());
        verify(coursePersistence, times(1)).existsById(courseId);
        verify(lessonProgressPersistence, times(1)).countLessonsByCourseId(courseId);
        verify(lessonProgressPersistence, times(1)).countCompletedLessonsByProfileAndCourse(profileId, courseId);
    }

    @Test
    @DisplayName("Should return zero percentage when course has no lessons")
    void case02() {
        when(getCurrentUserCase.execute(traceId, authorization)).thenReturn(currentUser());
        when(coursePersistence.existsById(courseId)).thenReturn(true);
        when(lessonProgressPersistence.countLessonsByCourseId(courseId)).thenReturn(0L);
        when(lessonProgressPersistence.countCompletedLessonsByProfileAndCourse(profileId, courseId)).thenReturn(0L);

        CourseProgressDTO progress = getCourseProgressCase.execute(traceId, courseId, authorization);

        assertEquals(0L, progress.totalLessons());
        assertEquals(0L, progress.completedLessons());
        assertEquals(new BigDecimal("0.00"), progress.percentage());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when course does not exist")
    void case03() {
        when(getCurrentUserCase.execute(traceId, authorization)).thenReturn(currentUser());
        when(coursePersistence.existsById(courseId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> getCourseProgressCase.execute(traceId, courseId, authorization));

        verify(lessonProgressPersistence, never()).countLessonsByCourseId(courseId);
        verify(lessonProgressPersistence, never()).countCompletedLessonsByProfileAndCourse(profileId, courseId);
    }

    private Identity currentUser() {
        return Identity.builder()
                .profile(Profile.builder()
                        .id(profileId)
                        .build())
                .build();
    }

}
