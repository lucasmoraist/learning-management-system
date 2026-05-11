package com.lucasmoraist.lms.application.usecases.user;

import com.lucasmoraist.lms.domain.model.Identity;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ListUsersCase {

    private final IdentityPersistence identityPersistence;

    public ListUsersCase(IdentityPersistence identityPersistence) {
        this.identityPersistence = identityPersistence;
    }

    public Page<Identity> execute(String traceId, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        log.info("[{}] - Listing users with pagination: page={}, size={}", traceId, page, size);

        return this.identityPersistence.findAll(pageable);
    }

}
