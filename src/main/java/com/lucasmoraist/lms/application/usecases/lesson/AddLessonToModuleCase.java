package com.lucasmoraist.lms.application.usecases.lesson;

import com.lucasmoraist.lms.adapter.web.dto.lesson.CreateLessonDTO;
import com.lucasmoraist.lms.domain.model.catalog.Lesson;
import com.lucasmoraist.lms.domain.model.catalog.Module;
import com.lucasmoraist.lms.infrastructure.database.persistence.LessonPersistence;
import com.lucasmoraist.lms.infrastructure.database.persistence.ModulePersistence;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class AddLessonToModuleCase {

    private final ModulePersistence modulePersistence;
    private final LessonPersistence lessonPersistence;

    public AddLessonToModuleCase(ModulePersistence modulePersistence, LessonPersistence lessonPersistence) {
        this.modulePersistence = modulePersistence;
        this.lessonPersistence = lessonPersistence;
    }

    public Lesson execute(String traceId, UUID moduleId, CreateLessonDTO dto) {
        log.debug("[{}] - Executing AddLessonToModuleCase for module ID {}", traceId, moduleId);
        Module module = this.modulePersistence.findById(moduleId)
                .orElseThrow(() -> {
                    log.error("[{}] - Module with ID {} not found", traceId, moduleId);
                    return new EntityNotFoundException("Module not found");
                });

        Integer position = module.getLessons() != null ? module.getLessons().size() + 1 : 1;
        log.debug("[{}] - Calculated position for new lesson: {}", traceId, position);

        Lesson lesson = Lesson.builder()
                .title(dto.title())
                .position(position)
//                .contentUrl() // TODO: Implementar lógica para salvar vídeo em um serviço de armazenamento e obter a URL
//                .durationInSeconds() // TODO: Implementar lógica para obter a duração do vídeo
                .module(module)
                .build();

        Lesson lessonSaved = this.lessonPersistence.save(lesson);
        log.debug("[{}] - Lesson saved with ID {}", traceId, lessonSaved.getId());
        return lessonSaved;
    }

}
