package com.lucasmoraist.lms.application.usecases.authentication;

import com.lucasmoraist.lms.domain.exceptions.AuthenticationException;
import com.lucasmoraist.lms.domain.gateway.TokenGateway;
import com.lucasmoraist.lms.domain.model.auth.Token;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RefreshRoleCase {

    private final TokenGateway tokenGateway;
    private final IdentityPersistence identityPersistence;

    public RefreshRoleCase(TokenGateway tokenGateway, IdentityPersistence identityPersistence) {
        this.tokenGateway = tokenGateway;
        this.identityPersistence = identityPersistence;
    }

    public Token execute(String traceId, Token token) {
        final String subject = this.tokenGateway.getSubjectFromToken(token.getAccessToken());

        Identity identity = this.identityPersistence.findById(UUID.fromString(subject))
                .orElseThrow(() -> {
                    log.warn("[{}] - No user found with id: {}", traceId, subject);
                    return new AuthenticationException("User not found");
                });

        return this.tokenGateway.generateToken(identity);
    }

}
