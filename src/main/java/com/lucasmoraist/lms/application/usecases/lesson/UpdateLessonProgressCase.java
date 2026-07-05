package com.lucasmoraist.lms.application.usecases.lesson;

import com.lucasmoraist.lms.application.usecases.certificate.IssueCertificateCase;
import com.lucasmoraist.lms.application.usecases.user.GetCurrentUserCase;
import com.lucasmoraist.lms.domain.gateway.CacheGateway;
import com.lucasmoraist.lms.domain.model.catalog.LessonProgress;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.infrastructure.database.persistence.LessonProgressPersistence;
import com.lucasmoraist.lms.infrastructure.database.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateLessonProgressCase {

    private final LessonProgressPersistence lessonProgressPersistence;
    private final CacheGateway cacheGateway;
    private final FindLessonByIdCase findLessonByIdCase;
    private final GetCurrentUserCase getCurrentUserCase;
    private final LessonRepository lessonRepository;
    private final IssueCertificateCase issueCertificateCase;
    private final Executor processorAsync = Executors.newVirtualThreadPerTaskExecutor();

    private static final String REDIS_KEY_PREFIX = "lms:progress:profile:%s:lesson:%s";

    public LessonProgress execute(String authorization, UUID lessonId, Integer currentSeconds, String traceId) {
        Identity currentUser = getCurrentUserCase.execute(traceId, authorization);
        UUID profileId = currentUser.getProfile().getId();

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

            boolean wasCompletedBefore = Boolean.TRUE.equals(progress.getCompleted());
            LocalDateTime previousUpdatedAt = progress.getUpdatedAt();
            Map<String, Object> lessonDetails = (Map<String, Object>) lesson.get("lesson");
            progress.updateProgress(currentSeconds, (Integer) lessonDetails.get("durationInSeconds"));

            // Expires in 7 days
            cacheGateway.set(cacheKey, progress, 7 * 24 * 60 * 60);

            if (progress.getCompleted() || previousUpdatedAt == null ||
                previousUpdatedAt.isBefore(LocalDateTime.now().minusMinutes(1))) {
                progress = lessonProgressPersistence.save(progress);
            }

            if (!wasCompletedBefore && Boolean.TRUE.equals(progress.getCompleted())) {
                processorAsync.execute(() -> tryIssueCertificate(traceId, profileId, lessonId));
            }

            return progress;
        } catch (ClassCastException ex) {
            log.error("[{}] - Error casting cached progress for profile {} and lesson {}: {}", traceId, profileId, lessonId, ex.getMessage());
            cacheGateway.delete(String.format(REDIS_KEY_PREFIX, profileId, lessonId));
            throw ex;
        }
    }

    private void tryIssueCertificate(String traceId, UUID profileId, UUID lessonId) {
        lessonRepository.findCourseIdByLessonId(lessonId).ifPresent(courseId -> {
            log.debug("[{}] - Lesson {} completed, checking certificate eligibility for course {}",
                    traceId, lessonId, courseId);
            issueCertificateCase.execute(traceId, profileId, courseId);
        });
    }

}
