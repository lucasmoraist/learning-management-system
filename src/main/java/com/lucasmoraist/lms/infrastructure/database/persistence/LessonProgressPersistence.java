package com.lucasmoraist.lms.infrastructure.database.persistence;

import com.lucasmoraist.lms.domain.model.catalog.LessonProgress;
import com.lucasmoraist.lms.infrastructure.database.entity.catalog.LessonProgressEntity;
import com.lucasmoraist.lms.infrastructure.database.repository.LessonProgressRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class LessonProgressPersistence {

    private final LessonProgressRepository lessonProgressRepository;
    private final ModelMapper modelMapper;

    public LessonProgressPersistence(LessonProgressRepository lessonProgressRepository, ModelMapper modelMapper) {
        this.lessonProgressRepository = lessonProgressRepository;
        this.modelMapper = modelMapper;
    }

    public Optional<LessonProgress> findByProfileAndLesson(UUID profileId, UUID lessonId) {
        return lessonProgressRepository.findByProfileIdAndLessonId(profileId, lessonId)
                .map(entity -> modelMapper.map(entity, LessonProgress.class));
    }

    public long countLessonsByCourseId(UUID courseId) {
        return lessonProgressRepository.countLessonsByCourseId(courseId);
    }

    public long countCompletedLessonsByProfileAndCourse(UUID profileId, UUID courseId) {
        return lessonProgressRepository.countCompletedLessonsByProfileIdAndCourseId(profileId, courseId);
    }

    public LessonProgress save(LessonProgress progress) {
        LessonProgressEntity entity = modelMapper.map(progress, LessonProgressEntity.class);
        LessonProgressEntity savedEntity = lessonProgressRepository.save(entity);
        return modelMapper.map(savedEntity, LessonProgress.class);
    }

}
