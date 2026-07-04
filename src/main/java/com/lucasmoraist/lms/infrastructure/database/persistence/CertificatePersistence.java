package com.lucasmoraist.lms.infrastructure.database.persistence;

import com.lucasmoraist.lms.domain.model.user.Certificate;
import com.lucasmoraist.lms.domain.model.user.Profile;
import com.lucasmoraist.lms.infrastructure.database.entity.user.CertificateEntity;
import com.lucasmoraist.lms.infrastructure.database.entity.user.ProfileEntity;
import com.lucasmoraist.lms.infrastructure.database.repository.CertificateRepository;
import com.lucasmoraist.lms.infrastructure.database.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CertificatePersistence {

    private final CertificateRepository certificateRepository;
    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper;

    public Optional<Certificate> findByProfileIdAndTitle(UUID profileId, String title) {
        return certificateRepository.findByProfile_IdAndTitle(profileId, title)
                .map(entity -> modelMapper.map(entity, Certificate.class));
    }

    public Optional<Certificate> findById(UUID certificateId) {
        return certificateRepository.findByIdWithProfile(certificateId)
                .map(entity -> modelMapper.map(entity, Certificate.class));
    }

    public List<Certificate> findByProfileId(UUID profileId) {
        return certificateRepository.findByProfile_IdOrderByIssuedAtDesc(profileId).stream()
                .map(entity -> modelMapper.map(entity, Certificate.class))
                .toList();
    }

    @Transactional
    public Certificate save(Certificate certificate) {
        CertificateEntity entity = modelMapper.map(certificate, CertificateEntity.class);

        ProfileEntity profileEntity = profileRepository.findById(certificate.getProfile().getId())
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        entity.setProfile(profileEntity);

        CertificateEntity savedEntity = certificateRepository.saveAndFlush(entity);
        return modelMapper.map(savedEntity, Certificate.class);
    }

}
