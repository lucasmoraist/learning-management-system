package com.lucasmoraist.lms.application.usecases.user;

import com.lucasmoraist.lms.application.utils.HeaderUtils;
import com.lucasmoraist.lms.domain.exceptions.AuthenticationException;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import com.lucasmoraist.lms.infrastructure.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class DeleteUserCase {

    private final TokenService tokenService;
    private final IdentityPersistence identityPersistence;

    public DeleteUserCase(TokenService tokenService, IdentityPersistence identityPersistence) {
        this.tokenService = tokenService;
        this.identityPersistence = identityPersistence;
    }

    public void execute(String traceId, String authorization) {
        final String token = HeaderUtils.getBearerToken(authorization);
        if (token == null) {
            log.warn("[{}] - No Bearer token found in Authorization header", traceId);
            throw new AuthenticationException("No Bearer token found in Authorization header");
        }

        final String subject = this.tokenService.getSubjectFromToken(token);
        this.identityPersistence.deleteByEntity(UUID.fromString(subject));
    }

}
