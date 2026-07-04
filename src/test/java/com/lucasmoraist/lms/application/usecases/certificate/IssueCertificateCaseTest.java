package com.lucasmoraist.lms.application.usecases.certificate;

import com.lucasmoraist.lms.domain.gateway.BucketGateway;
import com.lucasmoraist.lms.domain.gateway.CertificateGateway;
import com.lucasmoraist.lms.domain.model.catalog.Course;
import com.lucasmoraist.lms.domain.model.user.Certificate;
import com.lucasmoraist.lms.infrastructure.database.entity.user.ProfileEntity;
import com.lucasmoraist.lms.infrastructure.database.persistence.CertificatePersistence;
import com.lucasmoraist.lms.infrastructure.database.persistence.CoursePersistence;
import com.lucasmoraist.lms.infrastructure.database.persistence.LessonProgressPersistence;
import com.lucasmoraist.lms.infrastructure.database.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IssueCertificateCaseTest {

    @InjectMocks
    IssueCertificateCase issueCertificateCase;
    @Mock
    CoursePersistence coursePersistence;
    @Mock
    LessonProgressPersistence lessonProgressPersistence;
    @Mock
    CertificatePersistence certificatePersistence;
    @Mock
    ProfileRepository profileRepository;
    @Mock
    CertificateGateway certificateGateway;
    @Mock
    BucketGateway bucketGateway;

    String traceId;
    UUID profileId;
    UUID courseId;

    @BeforeEach
    void setUp() {
        traceId = "trace-123";
        profileId = UUID.randomUUID();
        courseId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should issue certificate when course is fully completed")
    void shouldIssueCertificateWhenCourseCompleted() {
        Course course = Course.builder()
                .id(courseId)
                .title("Java Avancado")
                .build();

        when(coursePersistence.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonProgressPersistence.countLessonsByCourseId(courseId)).thenReturn(2L);
        when(lessonProgressPersistence.countCompletedLessonsByProfileAndCourse(profileId, courseId)).thenReturn(2L);
        when(certificatePersistence.findByProfileIdAndTitle(profileId, course.getTitle())).thenReturn(Optional.empty());
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setId(profileId);
        profileEntity.setName("Ana Silva");
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profileEntity));
        when(certificateGateway.generatePdf(eq("Ana Silva"), eq("Java Avancado"), any(LocalDateTime.class)))
                .thenReturn(new byte[] {1, 2, 3});
        when(certificatePersistence.save(any(Certificate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Certificate> result = issueCertificateCase.execute(traceId, profileId, courseId);

        assertTrue(result.isPresent());
        assertEquals("Java Avancado", result.get().getTitle());
        verify(certificateGateway).uploadPdf(any(String.class), eq(new byte[] {1, 2, 3}));
        verify(certificatePersistence).save(any(Certificate.class));
    }

    @Test
    @DisplayName("Should skip issuance when course is not fully completed")
    void shouldSkipWhenCourseNotCompleted() {
        Course course = Course.builder()
                .id(courseId)
                .title("Java Avancado")
                .build();

        when(coursePersistence.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonProgressPersistence.countLessonsByCourseId(courseId)).thenReturn(4L);
        when(lessonProgressPersistence.countCompletedLessonsByProfileAndCourse(profileId, courseId)).thenReturn(2L);

        Optional<Certificate> result = issueCertificateCase.execute(traceId, profileId, courseId);

        assertTrue(result.isEmpty());
        verify(certificateGateway, never()).generatePdf(any(), any(), any());
        verify(certificatePersistence, never()).save(any());
    }

    @Test
    @DisplayName("Should return existing certificate when already issued")
    void shouldReturnExistingCertificate() {
        Course course = Course.builder()
                .id(courseId)
                .title("Java Avancado")
                .build();
        Certificate existing = Certificate.builder()
                .id(UUID.randomUUID())
                .title(course.getTitle())
                .description("certificates/existing.pdf")
                .build();

        when(coursePersistence.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonProgressPersistence.countLessonsByCourseId(courseId)).thenReturn(2L);
        when(lessonProgressPersistence.countCompletedLessonsByProfileAndCourse(profileId, courseId)).thenReturn(2L);
        when(certificatePersistence.findByProfileIdAndTitle(profileId, course.getTitle())).thenReturn(Optional.of(existing));

        Optional<Certificate> result = issueCertificateCase.execute(traceId, profileId, courseId);

        assertTrue(result.isPresent());
        assertEquals(existing.getId(), result.get().getId());
        verify(certificateGateway, never()).generatePdf(any(), any(), any());
        verify(certificatePersistence, never()).save(any());
    }

    @Test
    @DisplayName("Should build download URL from storage key")
    void shouldBuildDownloadUrl() {
        Certificate certificate = Certificate.builder()
                .description("certificates/profile/course/id.pdf")
                .build();

        when(bucketGateway.getPublicUrl("certificates/profile/course/id.pdf"))
                .thenReturn("https://storage.example/certificates/profile/course/id.pdf");

        String downloadUrl = issueCertificateCase.getDownloadUrl(certificate);

        assertEquals("https://storage.example/certificates/profile/course/id.pdf", downloadUrl);
    }

}
