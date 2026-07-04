package com.lucasmoraist.lms.application.usecases.lesson;

import com.lucasmoraist.lms.domain.exceptions.LessonNotFoundException;
import com.lucasmoraist.lms.domain.gateway.CacheGateway;
import com.lucasmoraist.lms.domain.model.catalog.LessonProgress;
import com.lucasmoraist.lms.infrastructure.database.persistence.LessonPersistence;
import com.lucasmoraist.lms.infrastructure.database.persistence.LessonProgressPersistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLessonProgressCaseTest {

    private static final String CACHE_KEY_TEMPLATE = "lms:progress:profile:%s:lesson:%s";

    @InjectMocks
    GetLessonProgressCase getLessonProgressCase;
    @Mock
    LessonProgressPersistence progressPersistence;
    @Mock
    CacheGateway cacheGateway;
    @Mock
    LessonPersistence lessonPersistence;

    UUID profileId;
    UUID lessonId;
    String cacheKey;

    @BeforeEach
    void setUp() {
        profileId = UUID.randomUUID();
        lessonId = UUID.randomUUID();
        cacheKey = String.format(CACHE_KEY_TEMPLATE, profileId, lessonId);
    }

    @Test
    @DisplayName("Should return cached lesson progress")
    void case01() {
        LessonProgress cachedProgress = LessonProgress.builder()
                .profileId(profileId)
                .lessonId(lessonId)
                .lastWatchedTimeInSeconds(120)
                .completed(true)
                .build();

        when(lessonPersistence.existsById(lessonId)).thenReturn(true);
        when(cacheGateway.get(cacheKey, LessonProgress.class)).thenReturn(Optional.of(cachedProgress));

        LessonProgress progress = getLessonProgressCase.execute(profileId, lessonId);

        assertEquals(120, progress.getLastWatchedTimeInSeconds());
        assertTrue(progress.getCompleted());
        verify(progressPersistence, never()).findByProfileAndLesson(profileId, lessonId);
    }

    @Test
    @DisplayName("Should return persisted progress when cache is empty")
    void case02() {
        LessonProgress persistedProgress = LessonProgress.builder()
                .profileId(profileId)
                .lessonId(lessonId)
                .lastWatchedTimeInSeconds(30)
                .completed(false)
                .build();

        when(lessonPersistence.existsById(lessonId)).thenReturn(true);
        when(cacheGateway.get(cacheKey, LessonProgress.class)).thenReturn(Optional.empty());
        when(progressPersistence.findByProfileAndLesson(profileId, lessonId)).thenReturn(Optional.of(persistedProgress));

        LessonProgress progress = getLessonProgressCase.execute(profileId, lessonId);

        assertEquals(30, progress.getLastWatchedTimeInSeconds());
        assertFalse(progress.getCompleted());
    }

    @Test
    @DisplayName("Should return default progress when cache and persistence are empty")
    void case03() {
        when(lessonPersistence.existsById(lessonId)).thenReturn(true);
        when(cacheGateway.get(cacheKey, LessonProgress.class)).thenReturn(Optional.empty());
        when(progressPersistence.findByProfileAndLesson(profileId, lessonId)).thenReturn(Optional.empty());

        LessonProgress progress = getLessonProgressCase.execute(profileId, lessonId);

        assertEquals(profileId, progress.getProfileId());
        assertEquals(lessonId, progress.getLessonId());
        assertEquals(0, progress.getLastWatchedTimeInSeconds());
        assertFalse(progress.getCompleted());
    }

    @Test
    @DisplayName("Should throw LessonNotFoundException when lesson does not exist")
    void case04() {
        when(lessonPersistence.existsById(lessonId)).thenReturn(false);

        assertThrows(LessonNotFoundException.class,
                () -> getLessonProgressCase.execute(profileId, lessonId));

        verify(cacheGateway, never()).get(cacheKey, LessonProgress.class);
        verify(progressPersistence, never()).findByProfileAndLesson(profileId, lessonId);
    }

    @Test
    @DisplayName("Should delete corrupted cache and rethrow ClassCastException")
    void case05() {
        when(lessonPersistence.existsById(lessonId)).thenReturn(true);
        when(cacheGateway.get(cacheKey, LessonProgress.class)).thenReturn(Optional.of("invalid-cache-value"));

        assertThrows(ClassCastException.class,
                () -> getLessonProgressCase.execute(profileId, lessonId));

        verify(cacheGateway, times(1)).delete(cacheKey);
    }

}
