package com.lucasmoraist.lms.application.usecases.authentication;

import com.lucasmoraist.lms.adapter.web.dto.auth.LoginDTO;
import com.lucasmoraist.lms.domain.exceptions.AuthenticationException;
import com.lucasmoraist.lms.domain.gateway.TokenGateway;
import com.lucasmoraist.lms.domain.model.Identity;
import com.lucasmoraist.lms.domain.model.Token;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GenerateTokenCase {

    private final IdentityPersistence identityPersistence;
    private final PasswordEncoder passwordEncoder;
    private final TokenGateway tokenGateway;

    public GenerateTokenCase(IdentityPersistence identityPersistence, PasswordEncoder passwordEncoder, TokenGateway tokenGateway) {
        this.identityPersistence = identityPersistence;
        this.passwordEncoder = passwordEncoder;
        this.tokenGateway = tokenGateway;
    }

    public Token execute(String traceId, LoginDTO dto) {
        Identity identity = this.identityPersistence.findByEmail(dto.email())
                .orElseThrow(() -> {
                    log.warn("[{}] - No user found with email: {}", traceId, dto.email());
                    return new AuthenticationException("Invalid email or password");
                });

        if (!this.passwordEncoder.matches(dto.password(), identity.getPassword())) {
            log.warn("[{}] - Invalid password for email: {}", traceId, dto.email());
            throw new AuthenticationException("Invalid email or password");
        }

        return this.tokenGateway.generateToken(identity);
    }

}
