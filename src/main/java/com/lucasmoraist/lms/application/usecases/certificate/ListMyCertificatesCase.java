package com.lucasmoraist.lms.application.usecases.certificate;

import com.lucasmoraist.lms.adapter.web.dto.certificate.CertificateDTO;
import com.lucasmoraist.lms.application.usecases.user.GetCurrentUserCase;
import com.lucasmoraist.lms.domain.model.user.Certificate;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.infrastructure.database.persistence.CertificatePersistence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListMyCertificatesCase {

    private final GetCurrentUserCase getCurrentUserCase;
    private final CertificatePersistence certificatePersistence;
    private final IssueCertificateCase issueCertificateCase;

    public List<CertificateDTO> execute(String traceId, String authorization) {
        Identity currentUser = getCurrentUserCase.execute(traceId, authorization);
        UUID profileId = currentUser.getProfile().getId();

        log.debug("[{}] - Listing certificates for profile {}", traceId, profileId);

        return certificatePersistence.findByProfileId(profileId).stream()
                .map(this::toDto)
                .toList();
    }

    private CertificateDTO toDto(Certificate certificate) {
        return new CertificateDTO(
                certificate.getId(),
                certificate.getTitle(),
                issueCertificateCase.getDownloadUrl(certificate),
                certificate.getIssuedAt()
        );
    }

}
