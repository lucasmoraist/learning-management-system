package com.lucasmoraist.lms.application.usecases.lesson;

import com.lucasmoraist.lms.domain.gateway.CacheGateway;
import com.lucasmoraist.lms.domain.model.catalog.LessonProgress;
import com.lucasmoraist.lms.infrastructure.database.persistence.LessonProgressPersistence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetLessonProgressCase {

    private final LessonProgressPersistence progressPersistence;
    private final CacheGateway cacheGateway;

    private static final String REDIS_KEY_PREFIX = "lms:progress:profile:%s:lesson:%s";

    public LessonProgress execute(UUID profileId, UUID lessonId) {
        try {
            String cacheKey = String.format(REDIS_KEY_PREFIX, profileId, lessonId);

            return (LessonProgress) cacheGateway.get(cacheKey, LessonProgress.class)
                    .orElseGet(() -> progressPersistence.findByProfileAndLesson(profileId, lessonId)
                            .orElse(LessonProgress.builder()
                                    .profileId(profileId)
                                    .lessonId(lessonId)
                                    .lastWatchedTimeInSeconds(0)
                                    .completed(false)
                                    .build()));
        } catch (ClassCastException ex) {
            log.error("Error casting cached progress for profile {} and lesson {}: {}", profileId, lessonId, ex.getMessage());
            cacheGateway.delete(String.format(REDIS_KEY_PREFIX, profileId, lessonId));
            throw ex;
        }
    }

}
