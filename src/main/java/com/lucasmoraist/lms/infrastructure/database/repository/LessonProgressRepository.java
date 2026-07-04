package com.lucasmoraist.lms.infrastructure.database.repository;

import com.lucasmoraist.lms.domain.model.catalog.LessonProgress;
import com.lucasmoraist.lms.infrastructure.database.entity.catalog.LessonProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface LessonProgressRepository extends JpaRepository<LessonProgressEntity, UUID> {

    Optional<LessonProgress> findByProfileIdAndLessonId(UUID profileId, UUID lessonId);

    @Query(value = """
            SELECT COUNT(l.id)
            FROM tb_lesson l
            INNER JOIN tb_module m ON m.id = l.module_id
            WHERE m.course_id = :courseId
            """, nativeQuery = true)
    long countLessonsByCourseId(@Param("courseId") UUID courseId);

    @Query(value = """
            SELECT COUNT(DISTINCT l.id)
            FROM tb_lesson l
            INNER JOIN tb_module m ON m.id = l.module_id
            INNER JOIN tb_lesson_progress lp ON lp.lesson_id = l.id
            WHERE m.course_id = :courseId
              AND lp.profile_id = :profileId
              AND lp.completed = TRUE
            """, nativeQuery = true)
    long countCompletedLessonsByProfileIdAndCourseId(
            @Param("profileId") UUID profileId,
            @Param("courseId") UUID courseId
    );

}
