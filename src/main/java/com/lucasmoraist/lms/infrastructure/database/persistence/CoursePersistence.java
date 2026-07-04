package com.lucasmoraist.lms.infrastructure.database.persistence;

import com.lucasmoraist.lms.domain.model.catalog.Course;
import com.lucasmoraist.lms.infrastructure.database.entity.catalog.CourseEntity;
import com.lucasmoraist.lms.infrastructure.database.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoursePersistence {

    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    public Optional<Course> findById(UUID courseId) {
        Optional<CourseEntity> entityOpt = this.courseRepository.findById(courseId);
        return entityOpt.map(entity -> this.modelMapper.map(entity, Course.class));
    }

    public boolean existsById(UUID courseId) {
        return this.courseRepository.existsById(courseId);
    }

    @Transactional
    public Course save(Course course) {
        CourseEntity entity = this.modelMapper.map(course, CourseEntity.class);

        CourseEntity savedEntity = this.courseRepository.saveAndFlush(entity);
        return this.modelMapper.map(savedEntity, Course.class);
    }

}
