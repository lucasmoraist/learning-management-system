package com.lucasmoraist.lms.infrastructure.database.repository;

import com.lucasmoraist.lms.infrastructure.database.entity.user.CertificateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<CertificateEntity, UUID> {

    Optional<CertificateEntity> findByProfile_IdAndTitle(UUID profileId, String title);

    List<CertificateEntity> findByProfile_IdOrderByIssuedAtDesc(UUID profileId);

    @Query("SELECT c FROM tb_certificate c JOIN FETCH c.profile WHERE c.id = :certificateId")
    Optional<CertificateEntity> findByIdWithProfile(@Param("certificateId") UUID certificateId);

}
