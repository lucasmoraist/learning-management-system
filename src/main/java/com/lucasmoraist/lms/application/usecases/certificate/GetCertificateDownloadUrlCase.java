package com.lucasmoraist.lms.application.usecases.certificate;

import com.lucasmoraist.lms.application.usecases.user.GetCurrentUserCase;
import com.lucasmoraist.lms.domain.model.user.Certificate;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.infrastructure.database.persistence.CertificatePersistence;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCertificateDownloadUrlCase {

    private final GetCurrentUserCase getCurrentUserCase;
    private final CertificatePersistence certificatePersistence;
    private final IssueCertificateCase issueCertificateCase;

    public String execute(String traceId, String authorization, UUID certificateId) {
        Identity currentUser = getCurrentUserCase.execute(traceId, authorization);
        UUID profileId = currentUser.getProfile().getId();

        Certificate certificate = certificatePersistence.findById(certificateId)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found"));

        if (certificate.getProfile() == null || !profileId.equals(certificate.getProfile().getId())) {
            log.warn("[{}] - Profile {} attempted to access certificate {} owned by another profile",
                    traceId, profileId, certificateId);
            throw new EntityNotFoundException("Certificate not found");
        }

        return issueCertificateCase.getDownloadUrl(certificate);
    }

}
