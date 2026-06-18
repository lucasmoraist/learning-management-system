package com.lucasmoraist.lms.application.usecases.lesson;

import com.lucasmoraist.lms.domain.gateway.BucketGateway;
import com.lucasmoraist.lms.domain.model.catalog.Lesson;
import com.lucasmoraist.lms.infrastructure.database.persistence.LessonPersistence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class FindLessonByIdCase {

    private final BucketGateway bucketGateway;
    private final LessonPersistence lessonPersistence;

    public FindLessonByIdCase(BucketGateway bucketGateway, LessonPersistence lessonPersistence) {
        this.bucketGateway = bucketGateway;
        this.lessonPersistence = lessonPersistence;
    }

    public Map<String, Object> execute(String traceId, UUID lessonId) {
        Lesson lesson = this.lessonPersistence.getLessonById(lessonId);
        log.debug("[{}] - Found lesson with ID {}: {}", traceId, lessonId, lesson);

        String publicUrl = this.bucketGateway.getPublicUrl(lesson.getContentUrl());
        log.debug("[{}] - Updated lesson content URL to public URL: {}", traceId, publicUrl);

        Map<String, Object> lessonDetails = new LinkedHashMap<>();
        lessonDetails.put("title", lesson.getTitle());
        lessonDetails.put("contentUrl", publicUrl);
        lessonDetails.put("position", lesson.getPosition());
        lessonDetails.put("durationInSeconds", lesson.getDurationInSeconds());

        return Map.of("lesson", lessonDetails);
    }

}
