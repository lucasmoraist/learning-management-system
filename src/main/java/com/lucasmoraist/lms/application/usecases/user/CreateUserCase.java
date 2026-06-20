package com.lucasmoraist.lms.application.usecases.user;

import com.lucasmoraist.lms.adapter.web.dto.user.CreateUserDTO;
import com.lucasmoraist.lms.domain.enums.DocumentType;
import com.lucasmoraist.lms.domain.enums.RoleType;
import com.lucasmoraist.lms.domain.exceptions.UniqueKeyDatabaseException;
import com.lucasmoraist.lms.domain.model.user.Document;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.domain.model.user.Profile;
import com.lucasmoraist.lms.domain.model.user.Role;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class CreateUserCase {

    private final IdentityPersistence identityPersistence;
    private final PasswordEncoder passwordEncoder;

    public CreateUserCase(IdentityPersistence identityPersistence, PasswordEncoder passwordEncoder) {
        this.identityPersistence = identityPersistence;
        this.passwordEncoder = passwordEncoder;
    }

    public void execute(String traceId, CreateUserDTO dto) {
        this.identityPersistence.findByEmail(dto.email())
                .ifPresent(it -> {
                    log.warn("[{}] - User with email {} already exists", traceId, dto.email());
                    throw new UniqueKeyDatabaseException("User with email " + dto.email() + " already exists");
                });

        Set<Role> roles = Set.of(
                Role.builder()
                        .name(RoleType.USER)
                        .build()
        );

        List<Document> documents = List.of(Document.builder()
                .number(dto.document())
                .type(DocumentType.findDocumentByValue(dto.document()))
                .build());

        Profile profile = Profile.builder()
                .name(dto.name())
                .birthDate(dto.birthDate())
                .documents(documents)
                .build();

        Identity newIdentity = Identity.builder()
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .roles(roles)
                .profile(profile)
                .isActive(false)
                .build();
        log.debug("[{}] - Creating new user with email: {}", traceId, dto.email());

        Identity createdIdentity = identityPersistence.save(newIdentity);
        log.debug("[{}] - User created with ID: {}", traceId, createdIdentity.getId());
    }

}
