package com.lucasmoraist.lms.infrastructure.database.repository;

import com.lucasmoraist.lms.infrastructure.database.entity.catalog.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<LessonEntity, UUID> {

    @Query("""
            SELECT l.module.course.id
            FROM tb_lesson l
            WHERE l.id = :lessonId
            """)
    Optional<UUID> findCourseIdByLessonId(@Param("lessonId") UUID lessonId);

}
