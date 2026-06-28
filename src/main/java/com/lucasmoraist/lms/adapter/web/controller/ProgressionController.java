package com.lucasmoraist.lms.adapter.web.controller;

import com.lucasmoraist.lms.adapter.web.dto.lesson.UpdateProgressDTO;
import com.lucasmoraist.lms.application.usecases.lesson.GetLessonProgressCase;
import com.lucasmoraist.lms.application.usecases.lesson.UpdateLessonProgressCase;
import com.lucasmoraist.lms.application.utils.TraceIdUtils;
import com.lucasmoraist.lms.domain.model.catalog.LessonProgress;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/progress")
public class ProgressionController {

    private final UpdateLessonProgressCase updateLessonProgressCase;
    private final GetLessonProgressCase getLessonProgressCase;

    @PostMapping("/lessons/{lessonId}/heartbeat")
    public ResponseEntity<LessonProgress> heartbeat(
            @PathVariable UUID lessonId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid UpdateProgressDTO dto
    ) {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Received heartbeat for lesson {} with current seconds {}", traceId, lessonId, dto.currentSeconds());

        LessonProgress progress = updateLessonProgressCase.execute(authorization, lessonId, dto.currentSeconds(),traceId);
        return ResponseEntity.ok(progress);
    }

    // Busca o progresso para saber de onde recomeçar o player de vídeo
    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<LessonProgress> getProgress(
            @PathVariable UUID lessonId,
            @RequestParam UUID profileId
    ) {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Fetching progress for lesson {} from profile {}", traceId, lessonId, profileId);

        LessonProgress progress = getLessonProgressCase.execute(profileId, lessonId);
        return ResponseEntity.ok(progress);
    }

}
