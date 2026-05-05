package com.lucasmoraist.lms.application.usecases.user;

import com.lucasmoraist.lms.adapter.web.dto.user.UpdateUserDTO;
import com.lucasmoraist.lms.domain.model.Identity;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateUserCase {

    private final GetCurrentUserCase getCurrentUserCase;
    private final IdentityPersistence identityPersistence;
    private final ModelMapper modelMapper;

    public UpdateUserCase(GetCurrentUserCase getCurrentUserCase, IdentityPersistence identityPersistence, ModelMapper modelMapper) {
        this.getCurrentUserCase = getCurrentUserCase;
        this.identityPersistence = identityPersistence;
        this.modelMapper = modelMapper;
    }

    public Identity execute(String traceId, String authorization, UpdateUserDTO dto) {
        log.debug("[{}] - Updating user | Payload: {}", traceId, dto.toString());
        Identity identity = this.getCurrentUserCase.execute(traceId, authorization);

        this.modelMapper.map(dto, identity);

        return this.identityPersistence.save(identity);
    }

}
