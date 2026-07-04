package com.lucasmoraist.lms.domain.gateway;

import java.time.LocalDateTime;

public interface CertificateGateway {

    byte[] generatePdf(String studentName, String courseTitle, LocalDateTime issuedAt);

    void uploadPdf(String storageKey, byte[] pdfContent);

}
