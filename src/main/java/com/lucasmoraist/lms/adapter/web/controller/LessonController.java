package com.lucasmoraist.lms.adapter.web.controller;

import com.lucasmoraist.lms.adapter.web.dto.lesson.CreateLessonDTO;
import com.lucasmoraist.lms.application.usecases.lesson.AddLessonToModuleCase;
import com.lucasmoraist.lms.application.utils.TraceIdUtils;
import com.lucasmoraist.lms.domain.model.catalog.Lesson;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lessons")
public class LessonController {

    private final AddLessonToModuleCase addLessonToModuleCase;

    @PostMapping("/add/{moduleId}")
    public ResponseEntity<Void> addModuleToCourse(
            @PathVariable UUID moduleId,
            @Valid @RequestBody CreateLessonDTO dto
    ) {
        final String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Adding module to course with ID {}", traceId, moduleId);

        Lesson module = this.addLessonToModuleCase.execute(traceId, moduleId, dto);

        URI location = URI.create("/api/v1/lessons/" + module.getId());
        return ResponseEntity.created(location).build();
    }

}
