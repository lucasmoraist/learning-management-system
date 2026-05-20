package com.lucasmoraist.lms.application.usecases.course;

import com.lucasmoraist.lms.application.mapper.CatalogMapper;
import com.lucasmoraist.lms.domain.model.catalog.Course;
import com.lucasmoraist.lms.infrastructure.database.persistence.CoursePersistence;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class FindCourseByIdCase {

    private final CoursePersistence coursePersistence;

    public FindCourseByIdCase(CoursePersistence coursePersistence) {
        this.coursePersistence = coursePersistence;
    }

    public Map<String, Object> execute(String traceId, UUID courseId) {
        log.debug("[{}] - Executing FindCourseByIdCase for course ID {}", traceId, courseId);
        Course course = this.coursePersistence.findById(courseId)
                .orElseThrow(() -> {
                    log.error("[{}] - Course with ID {} not found", traceId, courseId);
                    return new EntityNotFoundException("Course not found");
                });

        Map<String, Object> courseDetails = new LinkedHashMap<>();
        courseDetails.put("course", CatalogMapper.mapCourseToResponse(course));
        log.debug("[{} - Course details for ID {}: {}", traceId, courseId, courseDetails);

        return courseDetails;
    }

}
