package com.lucasmoraist.lms.infrastructure.database.repository;

import com.lucasmoraist.lms.infrastructure.database.entity.catalog.CourseEntity;
import com.lucasmoraist.lms.infrastructure.database.entity.catalog.LessonEntity;
import com.lucasmoraist.lms.infrastructure.database.entity.catalog.LessonProgressEntity;
import com.lucasmoraist.lms.infrastructure.database.entity.catalog.ModuleEntity;
import com.lucasmoraist.lms.infrastructure.database.entity.user.IdentityEntity;
import com.lucasmoraist.lms.infrastructure.database.entity.user.ProfileEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("default")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LessonProgressRepositoryIntegrationTest {

    @Autowired
    LessonProgressRepository lessonProgressRepository;
    @Autowired
    LessonRepository lessonRepository;
    @Autowired
    ModuleRepository moduleRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    IdentityRepository identityRepository;

    @Test
    @DisplayName("Should persist lesson progress and count completed lessons by course")
    void case01() {
        IdentityEntity identity = saveIdentityWithProfile();
        CourseEntity course = saveCourse(identity);
        ModuleEntity module = saveModule(course);
        LessonEntity completedLesson = saveLesson(module, "Lesson 1", 1);
        LessonEntity pendingLesson = saveLesson(module, "Lesson 2", 2);

        saveProgress(identity.getProfile().getId(), completedLesson.getId(), 95, true);
        saveProgress(identity.getProfile().getId(), pendingLesson.getId(), 20, false);

        long totalLessons = lessonProgressRepository.countLessonsByCourseId(course.getId());
        long completedLessons = lessonProgressRepository.countCompletedLessonsByProfileIdAndCourseId(
                identity.getProfile().getId(),
                course.getId()
        );

        assertThat(totalLessons).isEqualTo(2);
        assertThat(completedLessons).isEqualTo(1);
        assertThat(lessonProgressRepository.findByProfileIdAndLessonId(
                identity.getProfile().getId(),
                completedLesson.getId()
        )).isPresent()
                .get()
                .extracting(LessonProgressEntity::getCompleted)
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Should not count completed lessons from another course")
    void case02() {
        IdentityEntity identity = saveIdentityWithProfile();
        CourseEntity targetCourse = saveCourse(identity);
        ModuleEntity targetModule = saveModule(targetCourse);
        saveLesson(targetModule, "Target lesson", 1);

        CourseEntity anotherCourse = saveCourse(identity);
        ModuleEntity anotherModule = saveModule(anotherCourse);
        LessonEntity anotherLesson = saveLesson(anotherModule, "Another lesson", 1);
        saveProgress(identity.getProfile().getId(), anotherLesson.getId(), 100, true);

        long completedLessons = lessonProgressRepository.countCompletedLessonsByProfileIdAndCourseId(
                identity.getProfile().getId(),
                targetCourse.getId()
        );

        assertThat(completedLessons).isZero();
    }

    private IdentityEntity saveIdentityWithProfile() {
        IdentityEntity identity = new IdentityEntity();
        identity.setEmail(UUID.randomUUID() + "@email.com");
        identity.setPassword("password123");
        identity.setIsActive(true);

        ProfileEntity profile = new ProfileEntity();
        profile.setName("John Doe");
        profile.setBirthDate(LocalDate.of(2000, 1, 1));
        profile.setIdentity(identity);
        identity.setProfile(profile);

        return identityRepository.saveAndFlush(identity);
    }

    private CourseEntity saveCourse(IdentityEntity instructor) {
        CourseEntity course = new CourseEntity();
        course.setTitle("Java Course");
        course.setDescription("Course description");
        course.setInstructor(instructor);
        return courseRepository.saveAndFlush(course);
    }

    private ModuleEntity saveModule(CourseEntity course) {
        ModuleEntity module = new ModuleEntity();
        module.setTitle("Module 1");
        module.setPosition(1);
        module.setCourse(course);
        return moduleRepository.saveAndFlush(module);
    }

    private LessonEntity saveLesson(ModuleEntity module, String title, Integer position) {
        LessonEntity lesson = new LessonEntity();
        lesson.setTitle(title);
        lesson.setContentUrl(title.toLowerCase().replace(" ", "-") + ".mp4");
        lesson.setPosition(position);
        lesson.setDurationInSeconds(100);
        lesson.setModule(module);
        return lessonRepository.saveAndFlush(lesson);
    }

    private void saveProgress(UUID profileId, UUID lessonId, Integer lastWatchedTimeInSeconds, Boolean completed) {
        LessonProgressEntity progress = new LessonProgressEntity();
        progress.setProfileId(profileId);
        progress.setLessonId(lessonId);
        progress.setLastWatchedTimeInSeconds(lastWatchedTimeInSeconds);
        progress.setCompleted(completed);
        lessonProgressRepository.saveAndFlush(progress);
    }

}
