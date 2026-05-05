package com.lucasmoraist.lms.application.usecases.user;

import com.lucasmoraist.lms.application.utils.HeaderUtils;
import com.lucasmoraist.lms.domain.exceptions.AuthenticationException;
import com.lucasmoraist.lms.domain.model.Identity;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import com.lucasmoraist.lms.infrastructure.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class GetCurrentUserCase {

    private final TokenService tokenService;
    private final IdentityPersistence identityPersistence;

    public GetCurrentUserCase(TokenService tokenService, IdentityPersistence identityPersistence) {
        this.tokenService = tokenService;
        this.identityPersistence = identityPersistence;
    }

    public Identity execute(String traceId, String authorization) {
        final String token = HeaderUtils.getBearerToken(authorization);
        if (token == null) {
            log.warn("[{}] - No Bearer token found in Authorization header", traceId);
            throw new AuthenticationException("No Bearer token found in Authorization header");
        }

        final String subject = this.tokenService.getSubjectFromToken(token);
        return this.identityPersistence.findById(UUID.fromString(subject))
                .orElseThrow(() -> {
                    log.warn("[{}] - User not found with id: {}", traceId, subject);
                    return new AuthenticationException("User not found with id: " + subject);
                });
    }

}
