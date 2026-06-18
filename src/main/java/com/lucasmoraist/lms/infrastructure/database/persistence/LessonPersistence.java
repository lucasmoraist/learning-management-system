package com.lucasmoraist.lms.infrastructure.database.persistence;

import com.lucasmoraist.lms.domain.model.catalog.Lesson;
import com.lucasmoraist.lms.infrastructure.database.entity.catalog.LessonEntity;
import com.lucasmoraist.lms.infrastructure.database.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LessonPersistence {

    private final LessonRepository lessonRepository;
    private final ModelMapper modelMapper;

    public Lesson getLessonById(UUID lessonId) {
        LessonEntity entity = this.lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        return this.modelMapper.map(entity, Lesson.class);
    }

    public Lesson save(Lesson lesson) {
        LessonEntity entity = this.modelMapper.map(lesson, LessonEntity.class);
        LessonEntity savedEntity = this.lessonRepository.save(entity);
        return this.modelMapper.map(savedEntity, Lesson.class);
    }

}
