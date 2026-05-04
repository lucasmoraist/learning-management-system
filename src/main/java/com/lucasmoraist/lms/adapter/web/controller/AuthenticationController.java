package com.lucasmoraist.lms.adapter.web.controller;

import com.lucasmoraist.lms.adapter.web.dto.auth.LoginDTO;
import com.lucasmoraist.lms.application.usecases.authentication.GenerateTokenCase;
import com.lucasmoraist.lms.application.utils.TraceIdUtils;
import com.lucasmoraist.lms.domain.model.Token;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final GenerateTokenCase generateTokenCase;

    @PostMapping
    public ResponseEntity<Token> authentication(@Valid @RequestBody LoginDTO dto) {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Received authentication request for email: {}", traceId, dto.email());

        Token token = this.generateTokenCase.execute(traceId, dto);

        return ResponseEntity.ok(token);
    }

}
