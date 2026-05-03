package com.lucasmoraist.lms.application.application.usecases.user;

import com.lucasmoraist.lms.adapter.web.dto.user.CreateUserDTO;
import com.lucasmoraist.lms.domain.model.Identity;
import com.lucasmoraist.lms.domain.model.Profile;
import com.lucasmoraist.lms.domain.model.Role;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class CreateUserCase {

    private final IdentityPersistence identityPersistence;

    public CreateUserCase(IdentityPersistence identityPersistence) {
        this.identityPersistence = identityPersistence;
    }

    public void execute(String traceId, CreateUserDTO dto) {
        Set<Role> roles = Set.of(
                Role.builder()
                        .name(dto.role())
                        .build()
        );

        Profile profile = Profile.builder()
                .name(dto.name())
                .birthDate(dto.birthDate())
                .build();

        Identity newIdentity = Identity.builder()
                .email(dto.email())
                .password(dto.password())
                .roles(roles)
                .profile(profile)
                .isActive(false)
                .build();
        log.debug("[{}] - Creating new user with email: {}", traceId, dto.email());

        Identity createdIdentity = identityPersistence.save(newIdentity);
        log.debug("[{}] - User created with ID: {}", traceId, createdIdentity.getId());
    }

}
