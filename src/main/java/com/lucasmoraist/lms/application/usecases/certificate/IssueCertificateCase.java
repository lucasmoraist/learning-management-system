package com.lucasmoraist.lms.application.usecases.certificate;

import com.lucasmoraist.lms.domain.gateway.BucketGateway;
import com.lucasmoraist.lms.domain.gateway.CertificateGateway;
import com.lucasmoraist.lms.domain.model.catalog.Course;
import com.lucasmoraist.lms.domain.model.user.Certificate;
import com.lucasmoraist.lms.domain.model.user.Profile;
import com.lucasmoraist.lms.infrastructure.database.persistence.CertificatePersistence;
import com.lucasmoraist.lms.infrastructure.database.persistence.CoursePersistence;
import com.lucasmoraist.lms.infrastructure.database.persistence.LessonProgressPersistence;
import com.lucasmoraist.lms.infrastructure.database.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssueCertificateCase {

    private static final BigDecimal COMPLETION_THRESHOLD = new BigDecimal("100.00");

    private final CoursePersistence coursePersistence;
    private final LessonProgressPersistence lessonProgressPersistence;
    private final CertificatePersistence certificatePersistence;
    private final ProfileRepository profileRepository;
    private final CertificateGateway certificateGateway;
    private final BucketGateway bucketGateway;

    @Transactional
    public Optional<Certificate> execute(String traceId, UUID profileId, UUID courseId) {
        log.debug("[{}] - Evaluating certificate issuance for profile {} and course {}", traceId, profileId, courseId);

        Course course = coursePersistence.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        if (!isCourseCompleted(profileId, courseId)) {
            log.debug("[{}] - Course {} is not fully completed for profile {}", traceId, courseId, profileId);
            return Optional.empty();
        }

        if (certificatePersistence.findByProfileIdAndTitle(profileId, course.getTitle()).isPresent()) {
            log.info("[{}] - Certificate already issued for profile {} and course {}", traceId, profileId, courseId);
            return certificatePersistence.findByProfileIdAndTitle(profileId, course.getTitle());
        }

        String studentName = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"))
                .getName();

        LocalDateTime issuedAt = LocalDateTime.now();
        UUID certificateId = UUID.randomUUID();
        String storageKey = buildStorageKey(profileId, courseId, certificateId);

        byte[] pdfContent = certificateGateway.generatePdf(studentName, course.getTitle(), issuedAt);
        certificateGateway.uploadPdf(storageKey, pdfContent);

        Certificate certificate = Certificate.builder()
                .id(certificateId)
                .title(course.getTitle())
                .description(storageKey)
                .issuedAt(issuedAt)
                .profile(Profile.builder().id(profileId).build())
                .build();

        Certificate savedCertificate = certificatePersistence.save(certificate);
        log.info("[{}] - Certificate {} issued for profile {} and course {}", traceId, certificateId, profileId, courseId);

        return Optional.of(savedCertificate);
    }

    public String getDownloadUrl(Certificate certificate) {
        return bucketGateway.getPublicUrl(certificate.getDescription());
    }

    private boolean isCourseCompleted(UUID profileId, UUID courseId) {
        long totalLessons = lessonProgressPersistence.countLessonsByCourseId(courseId);
        if (totalLessons == 0) {
            return false;
        }

        long completedLessons = lessonProgressPersistence.countCompletedLessonsByProfileAndCourse(profileId, courseId);
        BigDecimal percentage = BigDecimal.valueOf(completedLessons)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalLessons), 2, RoundingMode.HALF_UP);

        return percentage.compareTo(COMPLETION_THRESHOLD) >= 0;
    }

    private String buildStorageKey(UUID profileId, UUID courseId, UUID certificateId) {
        return String.format("certificates/%s/%s/%s.pdf", profileId, courseId, certificateId);
    }

}
