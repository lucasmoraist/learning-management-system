package com.lucasmoraist.lms.infrastructure.database.persistence;

import com.lucasmoraist.lms.domain.enums.RoleType;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.infrastructure.database.entity.user.DocumentEntity;
import com.lucasmoraist.lms.infrastructure.database.entity.user.IdentityEntity;
import com.lucasmoraist.lms.infrastructure.database.entity.user.ProfileEntity;
import com.lucasmoraist.lms.infrastructure.database.repository.IdentityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Identity> findAll(Pageable pageable) {
        Page<IdentityEntity> identityEntities = this.identityRepository.findAll(pageable);
        return identityEntities.map(identityEntity -> this.modelMapper.map(identityEntity, Identity.class));
    }

    @Transactional
    public Identity save(Identity identity) {
        IdentityEntity identityEntity = this.modelMapper.map(identity, IdentityEntity.class);

        if (identityEntity.getProfile() != null) {
            ProfileEntity profile = identityEntity.getProfile();
            profile.setIdentity(identityEntity);

            if (profile.getDocuments() != null) {
                for (DocumentEntity documentEntity : profile.getDocuments()) {
                    documentEntity.setProfile(profile);
                }
            }
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
        IdentityEntity identity = getIdentityEntityById(id);
        this.identityRepository.delete(identity);
    }

    @Transactional
    public void updateRole(Identity identity, RoleType roleType) {
        IdentityEntity identityEntity = getIdentityEntityById(identity.getId());
        identityEntity.getRoles()
                .forEach(roleEntity -> roleEntity.setName(roleType));

        IdentityEntity updatedIdentity = this.identityRepository.save(identityEntity);
        log.info("Updated role for user with id {} to {}", updatedIdentity.getId(), roleType);
    }

    private IdentityEntity getIdentityEntityById(UUID id) {
        return this.identityRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new EntityNotFoundException("User not found with id: " + id);
                });
    }

}
