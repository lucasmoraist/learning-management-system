package com.lucasmoraist.lms.adapter.web.controller;

import com.lucasmoraist.lms.adapter.web.dto.certificate.CertificateDTO;
import com.lucasmoraist.lms.application.usecases.certificate.GetCertificateDownloadUrlCase;
import com.lucasmoraist.lms.application.usecases.certificate.ListMyCertificatesCase;
import com.lucasmoraist.lms.application.utils.TraceIdUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/certificates")
public class CertificateController {

    private final ListMyCertificatesCase listMyCertificatesCase;
    private final GetCertificateDownloadUrlCase getCertificateDownloadUrlCase;

    @GetMapping("/me")
    public ResponseEntity<List<CertificateDTO>> listMyCertificates(
            @RequestHeader("Authorization") String authorization
    ) {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Listing certificates for current user", traceId);

        List<CertificateDTO> certificates = listMyCertificatesCase.execute(traceId, authorization);
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/{certificateId}/download")
    public ResponseEntity<Void> downloadCertificate(
            @PathVariable UUID certificateId,
            @RequestHeader("Authorization") String authorization
    ) {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Download requested for certificate {}", traceId, certificateId);

        String downloadUrl = getCertificateDownloadUrlCase.execute(traceId, authorization, certificateId);
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, downloadUrl)
                .build();
    }

}
