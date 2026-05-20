package com.lucasmoraist.lms.application.usecases.module;

import com.lucasmoraist.lms.adapter.web.dto.module.CreateModuleDTO;
import com.lucasmoraist.lms.domain.model.catalog.Course;
import com.lucasmoraist.lms.domain.model.catalog.Module;
import com.lucasmoraist.lms.infrastructure.database.persistence.CoursePersistence;
import com.lucasmoraist.lms.infrastructure.database.persistence.ModulePersistence;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class AddModuleToCourseCase {

    private final CoursePersistence coursePersistence;
    private final ModulePersistence modulePersistence;

    public AddModuleToCourseCase(CoursePersistence coursePersistence, ModulePersistence modulePersistence) {
        this.coursePersistence = coursePersistence;
        this.modulePersistence = modulePersistence;
    }

    public Module execute(String traceId, UUID courseId, CreateModuleDTO dto) {
        log.debug("[{}] - Executing AddModuleToCourseCase for course ID {}", traceId, courseId);
        Course course = this.coursePersistence.findById(courseId)
                .orElseThrow(() -> {
                    log.error("[{}] - Course with ID {} not found", traceId, courseId);
                    return new EntityNotFoundException("Course not found");
                });

        Integer position = course.getModules() != null ? course.getModules().size() + 1 : 1;
        log.debug("[{}] - Calculated position for new module: {}", traceId, position);

        Module module = Module.builder()
                .title(dto.title())
                .position(position)
                .course(course)
                .build();

        Module moduleSaved = this.modulePersistence.save(module);
        log.debug("[{}] - Module saved with ID {}", traceId, moduleSaved.getId());
        return moduleSaved;
    }

}
