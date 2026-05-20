package com.lucasmoraist.lms.adapter.web.controller;

import com.lucasmoraist.lms.adapter.web.dto.module.CreateModuleDTO;
import com.lucasmoraist.lms.application.usecases.module.AddModuleToCourseCase;
import com.lucasmoraist.lms.application.utils.TraceIdUtils;
import com.lucasmoraist.lms.domain.model.catalog.Module;
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
@RequestMapping("/api/v1/modules")
public class ModuleController {

    private final AddModuleToCourseCase addModuleToCourseCase;

    @PostMapping("/add/{courseId}")
    public ResponseEntity<Void> addModuleToCourse(
            @PathVariable UUID courseId,
            @Valid @RequestBody CreateModuleDTO dto
    ) {
        final String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Adding module to course with ID {}", traceId, courseId);

        Module module = this.addModuleToCourseCase.execute(traceId, courseId, dto);

        URI location = URI.create("/api/v1/modules/"+module.getId());
        return ResponseEntity.created(location).build();
    }

}
