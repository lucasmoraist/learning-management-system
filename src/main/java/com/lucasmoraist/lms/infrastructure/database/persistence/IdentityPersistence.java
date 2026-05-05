package com.lucasmoraist.lms.infrastructure.database.persistence;

import com.lucasmoraist.lms.domain.model.Identity;
import com.lucasmoraist.lms.infrastructure.database.entity.IdentityEntity;
import com.lucasmoraist.lms.infrastructure.database.repository.IdentityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class IdentityPersistence {

    private final IdentityRepository identityRepository;
    private final ModelMapper modelMapper;

    public IdentityPersistence(IdentityRepository identityRepository, ModelMapper modelMapper) {
        this.identityRepository = identityRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public Identity save(Identity identity) {
        IdentityEntity identityEntity = this.modelMapper.map(identity, IdentityEntity.class);

        if (identityEntity.getProfile() != null) {
            identityEntity.getProfile().setIdentity(identityEntity);
        }

        IdentityEntity identityEntitySaved = this.identityRepository.saveAndFlush(identityEntity);
        return this.modelMapper.map(identityEntitySaved, Identity.class);
    }

    @Transactional(readOnly = true)
    public Optional<Identity> findByEmail(String email) {
        Optional<IdentityEntity> identityEntityOptional = this.identityRepository.findByEmail(email);
        return identityEntityOptional.map(identityEntity -> this.modelMapper.map(identityEntity, Identity.class));
    }

    public Optional<Identity> findById(UUID id) {
        Optional<IdentityEntity> identityEntityOptional = this.identityRepository.findById(id);
        return identityEntityOptional.map(identityEntity -> this.modelMapper.map(identityEntity, Identity.class));
    }

    public void deleteByEntity(UUID id) {
        IdentityEntity identity = this.identityRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new EntityNotFoundException("User not found with id: " + id);
                });
        this.identityRepository.delete(identity);
    }

}
