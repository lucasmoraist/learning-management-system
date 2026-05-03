package com.lucasmoraist.lms.adapter.web.controller;

import com.lucasmoraist.lms.adapter.web.dto.user.CreateUserDTO;
import com.lucasmoraist.lms.application.usecases.user.CreateUserCase;
import com.lucasmoraist.lms.application.utils.TraceIdUtils;
import com.lucasmoraist.lms.domain.model.Identity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final CreateUserCase createUserCase;

    @PostMapping("register")
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserDTO dto) {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Creating new user", traceId);

        this.createUserCase.execute(traceId, dto);

        URI location = URI.create("/api/v1/auth/login");
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/me")
    public ResponseEntity<Identity> getCurrentUser() {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Fetching current user", traceId);

        Identity identity = null;
        return ResponseEntity.ok(identity);
    }

    @PatchMapping("/me/update")
    public ResponseEntity<Identity> updateCurrentUser() {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Updating current user", traceId);

        Identity identity = null;
        return ResponseEntity.ok(identity);
    }

    @DeleteMapping("/me/delete")
    public ResponseEntity<Void> deleteCurrentUser() {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Deleting current user", traceId);

        return ResponseEntity.noContent().build();
    }

}
