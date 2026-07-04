package com.lucasmoraist.lms.application.usecases.lesson;

import com.lucasmoraist.lms.application.usecases.certificate.IssueCertificateCase;
import com.lucasmoraist.lms.application.usecases.user.GetCurrentUserCase;
import com.lucasmoraist.lms.application.utils.TraceIdUtils;
import com.lucasmoraist.lms.domain.gateway.CacheGateway;
import com.lucasmoraist.lms.domain.model.catalog.LessonProgress;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.domain.model.user.Profile;
import com.lucasmoraist.lms.infrastructure.database.persistence.LessonProgressPersistence;
import com.lucasmoraist.lms.infrastructure.database.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateLessonProgressCaseTest {

    private static final String CACHE_KEY_TEMPLATE = "lms:progress:profile:%s:lesson:%s";

    @InjectMocks
    UpdateLessonProgressCase updateLessonProgressCase;
    @Mock
    LessonProgressPersistence lessonProgressPersistence;
    @Mock
    CacheGateway cacheGateway;
    @Mock
    FindLessonByIdCase findLessonByIdCase;
    @Mock
    GetCurrentUserCase getCurrentUserCase;
    @Mock
    LessonRepository lessonRepository;
    @Mock
    IssueCertificateCase issueCertificateCase;

    String traceId;
    String authorization;
    UUID profileId;
    UUID lessonId;
    String cacheKey;

    @BeforeEach
    void setUp() {
        traceId = TraceIdUtils.generateTraceId();
        authorization = "Bearer valid-token";
        profileId = UUID.randomUUID();
        lessonId = UUID.randomUUID();
        cacheKey = String.format(CACHE_KEY_TEMPLATE, profileId, lessonId);
    }

    @Test
    @DisplayName("Should create progress and mark lesson as completed when watched at least 95 percent")
    void case01() {
        when(getCurrentUserCase.execute(traceId, authorization)).thenReturn(currentUser());
        when(findLessonByIdCase.execute(traceId, lessonId)).thenReturn(lessonResponse(100));
        when(cacheGateway.get(cacheKey, LessonProgress.class)).thenReturn(Optional.empty());
        when(lessonProgressPersistence.findByProfileAndLesson(profileId, lessonId)).thenReturn(Optional.empty());
        when(lessonProgressPersistence.save(any(LessonProgress.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(lessonRepository.findCourseIdByLessonId(lessonId)).thenReturn(Optional.empty());

        LessonProgress progress = updateLessonProgressCase.execute(authorization, lessonId, 95, traceId);

        assertTrue(progress.getCompleted());
        assertEquals(95, progress.getLastWatchedTimeInSeconds());
        verify(cacheGateway, times(1)).set(cacheKey, progress, 7 * 24 * 60 * 60);
        verify(lessonProgressPersistence, times(1)).save(progress);
    }

    @Test
    @DisplayName("Should update cached progress without persisting when lesson is not completed and update is recent")
    void case02() {
        LessonProgress cachedProgress = LessonProgress.builder()
                .profileId(profileId)
                .lessonId(lessonId)
                .lastWatchedTimeInSeconds(10)
                .completed(false)
                .updatedAt(LocalDateTime.now())
                .build();

        when(getCurrentUserCase.execute(traceId, authorization)).thenReturn(currentUser());
        when(findLessonByIdCase.execute(traceId, lessonId)).thenReturn(lessonResponse(100));
        when(cacheGateway.get(cacheKey, LessonProgress.class)).thenReturn(Optional.of(cachedProgress));

        LessonProgress progress = updateLessonProgressCase.execute(authorization, lessonId, 50, traceId);

        assertFalse(progress.getCompleted());
        assertEquals(50, progress.getLastWatchedTimeInSeconds());
        verify(lessonProgressPersistence, never()).save(any(LessonProgress.class));
        verify(cacheGateway, times(1)).set(cacheKey, progress, 7 * 24 * 60 * 60);
    }

    @Test
    @DisplayName("Should persist progress when last persistence is older than one minute")
    void case03() {
        LessonProgress persistedProgress = LessonProgress.builder()
                .profileId(profileId)
                .lessonId(lessonId)
                .lastWatchedTimeInSeconds(10)
                .completed(false)
                .updatedAt(LocalDateTime.now().minusMinutes(2))
                .build();

        when(getCurrentUserCase.execute(traceId, authorization)).thenReturn(currentUser());
        when(findLessonByIdCase.execute(traceId, lessonId)).thenReturn(lessonResponse(100));
        when(cacheGateway.get(cacheKey, LessonProgress.class)).thenReturn(Optional.empty());
        when(lessonProgressPersistence.findByProfileAndLesson(profileId, lessonId)).thenReturn(Optional.of(persistedProgress));
        when(lessonProgressPersistence.save(any(LessonProgress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LessonProgress progress = updateLessonProgressCase.execute(authorization, lessonId, 60, traceId);

        assertFalse(progress.getCompleted());
        assertEquals(60, progress.getLastWatchedTimeInSeconds());
        verify(lessonProgressPersistence, times(1)).save(progress);
    }

    @Test
    @DisplayName("Should delete corrupted cache and rethrow ClassCastException")
    void case04() {
        when(getCurrentUserCase.execute(traceId, authorization)).thenReturn(currentUser());
        when(findLessonByIdCase.execute(traceId, lessonId)).thenReturn(lessonResponse(100));
        when(cacheGateway.get(cacheKey, LessonProgress.class)).thenReturn(Optional.of("invalid-cache-value"));

        assertThrows(ClassCastException.class,
                () -> updateLessonProgressCase.execute(authorization, lessonId, 10, traceId));

        verify(cacheGateway, times(1)).delete(cacheKey);
    }

    private Identity currentUser() {
        return Identity.builder()
                .profile(Profile.builder()
                        .id(profileId)
                        .build())
                .build();
    }

    private Map<String, Object> lessonResponse(Integer durationInSeconds) {
        return Map.of("lesson", Map.of("durationInSeconds", durationInSeconds));
    }

}
