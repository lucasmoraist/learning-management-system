package com.lucasmoraist.lms.adapter.web.dto.certificate;

import java.time.LocalDateTime;
import java.util.UUID;

public record CertificateDTO(
        UUID id,
        String title,
        String downloadUrl,
        LocalDateTime issuedAt
) {
}
