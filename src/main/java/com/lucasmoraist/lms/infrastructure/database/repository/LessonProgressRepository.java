package com.lucasmoraist.lms.infrastructure.database.repository;

import com.lucasmoraist.lms.domain.model.catalog.LessonProgress;
import com.lucasmoraist.lms.infrastructure.database.entity.catalog.LessonProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LessonProgressRepository extends JpaRepository<LessonProgressEntity, UUID> {

    Optional<LessonProgress> findByProfileIdAndLessonId(UUID profileId, UUID lessonId);

}
