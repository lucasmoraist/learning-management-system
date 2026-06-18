package com.lucasmoraist.lms.adapter.web.controller;

import com.lucasmoraist.lms.adapter.web.dto.lesson.CreateLessonDTO;
import com.lucasmoraist.lms.application.usecases.lesson.AddLessonToModuleCase;
import com.lucasmoraist.lms.application.utils.TraceIdUtils;
import com.lucasmoraist.lms.domain.model.catalog.Lesson;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lessons")
public class LessonController {

    private final AddLessonToModuleCase addLessonToModuleCase;

    @PostMapping(value = "/add/module/{moduleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> addModuleToCourse(
            @PathVariable UUID moduleId,
            @Valid @RequestPart("data") CreateLessonDTO dto,
            @RequestPart("video") MultipartFile video
    ) {
        final String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Adding module to course with ID {}", traceId, moduleId);

        Lesson lesson = this.addLessonToModuleCase.execute(traceId, moduleId, dto, video);

        URI location = URI.create("/api/v1/lessons/" + lesson.getId());
        return ResponseEntity.created(location).build();
    }

}
