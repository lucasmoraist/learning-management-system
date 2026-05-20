package com.lucasmoraist.lms.adapter.web.controller;

import com.lucasmoraist.lms.adapter.web.dto.course.CreateCourseDTO;
import com.lucasmoraist.lms.application.usecases.course.CreateCourseCase;
import com.lucasmoraist.lms.application.usecases.course.FindCourseByIdCase;
import com.lucasmoraist.lms.application.utils.TraceIdUtils;
import com.lucasmoraist.lms.domain.model.catalog.Course;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CreateCourseCase createCourseCase;
    private final FindCourseByIdCase findCourseByIdCase;

    @PostMapping("/create")
    public ResponseEntity<Void> createCourse(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateCourseDTO dto
            ) {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Creating new course", traceId);

        Course course = this.createCourseCase.execute(traceId, authorization, dto);

        URI location = URI.create("/api/v1/courses/" + course.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping("{courseId}")
    public ResponseEntity<Map<String, Object>> getCourse(@PathVariable UUID courseId) {
        final String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Fetching course with ID {}", traceId, courseId);

        Map<String, Object> courseDetails = this.findCourseByIdCase.execute(traceId, courseId);
        return ResponseEntity.ok(courseDetails);
    }

}
