package com.lucasmoraist.lms.infrastructure.certificate;

import com.lucasmoraist.lms.domain.gateway.BucketGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @InjectMocks
    CertificateService certificateService;
    @Mock
    BucketGateway bucketGateway;

    @Test
    @DisplayName("Should generate a non-empty PDF certificate")
    void shouldGeneratePdf() {
        byte[] pdf = certificateService.generatePdf("Maria Souza", "Spring Boot", LocalDateTime.of(2026, 7, 4, 10, 0));

        assertNotNull(pdf);
        assertTrue(pdf.length > 100);
        assertTrue(new String(pdf, 0, Math.min(4, pdf.length)).startsWith("%PDF"));
    }

    @Test
    @DisplayName("Should upload generated PDF to bucket")
    void shouldUploadPdf() {
        byte[] pdf = new byte[] {37, 80, 68, 70};

        certificateService.uploadPdf("certificates/test.pdf", pdf);

        verify(bucketGateway).uploadBytes("certificates/test.pdf", pdf, "application/pdf");
    }

}
