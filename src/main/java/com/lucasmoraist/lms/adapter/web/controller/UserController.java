package com.lucasmoraist.lms.adapter.web.controller;

import com.lucasmoraist.lms.adapter.web.dto.user.CreateUserDTO;
import com.lucasmoraist.lms.adapter.web.dto.user.UpdateUserDTO;
import com.lucasmoraist.lms.application.usecases.user.CreateUserCase;
import com.lucasmoraist.lms.application.usecases.user.DeleteUserCase;
import com.lucasmoraist.lms.application.usecases.user.GetCurrentUserCase;
import com.lucasmoraist.lms.application.usecases.user.ListUsersCase;
import com.lucasmoraist.lms.application.usecases.user.UpdateUserCase;
import com.lucasmoraist.lms.application.utils.TraceIdUtils;
import com.lucasmoraist.lms.domain.model.user.Identity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final CreateUserCase createUserCase;
    private final ListUsersCase listUsersCase;
    private final GetCurrentUserCase getCurrentUserCase;
    private final UpdateUserCase updateUserCase;
    private final DeleteUserCase deleteUserCase;

    @PostMapping("register")
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserDTO dto) {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Creating new user", traceId);

        this.createUserCase.execute(traceId, dto);

        URI location = URI.create("/api/v1/auth/login");
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/me")
    public ResponseEntity<Identity> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Fetching current user", traceId);

        Identity identity = this.getCurrentUserCase.execute(traceId, authorization);
        return ResponseEntity.ok(identity);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<Identity>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Listing users", traceId);

        Page<Identity> identities = this.listUsersCase.execute(traceId, page, size);
        return ResponseEntity.ok(identities);
    }

    @PatchMapping("/me/update")
    public ResponseEntity<Identity> updateCurrentUser(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody UpdateUserDTO dto
    ) {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Updating current user", traceId);

        Identity identity = this.updateUserCase.execute(traceId, authorization, dto);
        return ResponseEntity.ok(identity);
    }

    @DeleteMapping("/me/delete")
    public ResponseEntity<Void> deleteCurrentUser(@RequestHeader("Authorization") String authorization) {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Deleting current user", traceId);

        this.deleteUserCase.execute(traceId, authorization);
        return ResponseEntity.noContent().build();
    }

}
