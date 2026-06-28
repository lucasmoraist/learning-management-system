package com.lucasmoraist.lms.application.usecases.lesson;

import com.lucasmoraist.lms.domain.gateway.CacheGateway;
import com.lucasmoraist.lms.domain.model.catalog.LessonProgress;
import com.lucasmoraist.lms.infrastructure.database.persistence.LessonProgressPersistence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateLessonProgressCase {

    private final LessonProgressPersistence lessonProgressPersistence;
    private final CacheGateway cacheGateway;
    private final FindLessonByIdCase findLessonByIdCase;

    private static final String REDIS_KEY_PREFIX = "lms:progress:profile:%s:lesson:%s";

    public LessonProgress execute(UUID profileId, UUID lessonId, Integer currentSeconds, String traceId) {
        try {
            String cacheKey = String.format(REDIS_KEY_PREFIX, profileId, lessonId);

            Map<String, Object> lesson = findLessonByIdCase.execute(traceId, lessonId);

            LessonProgress progress = (LessonProgress) cacheGateway.get(cacheKey, LessonProgress.class)
                    .orElseGet(() -> lessonProgressPersistence.findByProfileAndLesson(profileId, lessonId)
                            .orElseGet(() -> LessonProgress.builder()
                                    .profileId(profileId)
                                    .lessonId(lessonId)
                                    .completed(false)
                                    .build())
                    );
            log.debug("[{}] - Current progress for profile {} and lesson {}: {}", traceId, profileId, lessonId, progress);

            progress.updateProgress(currentSeconds, (Integer) lesson.get("durationInSeconds"));

            // Expires in 7 days
            cacheGateway.set(cacheKey, progress, 7 * 24 * 60 * 60);

            if (progress.getCompleted() || progress.getUpdatedAt() == null ||
                progress.getUpdatedAt().isBefore(LocalDateTime.now().minusMinutes(1))) {
                progress = lessonProgressPersistence.save(progress);
            }

            return progress;
        } catch (ClassCastException ex) {
            log.error("[{}] - Error casting cached progress for profile {} and lesson {}: {}", traceId, profileId, lessonId, ex.getMessage());
            cacheGateway.delete(String.format(REDIS_KEY_PREFIX, profileId, lessonId));
            throw ex;
        }
    }

}
