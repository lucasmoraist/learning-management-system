package com.lucasmoraist.lms.application.usecases.lesson;

import com.lucasmoraist.lms.adapter.web.dto.lesson.CreateLessonDTO;
import com.lucasmoraist.lms.application.utils.VideoUtils;
import com.lucasmoraist.lms.domain.gateway.BucketGateway;
import com.lucasmoraist.lms.domain.model.catalog.Lesson;
import com.lucasmoraist.lms.domain.model.catalog.Module;
import com.lucasmoraist.lms.infrastructure.database.persistence.LessonPersistence;
import com.lucasmoraist.lms.infrastructure.database.persistence.ModulePersistence;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Component
public class AddLessonToModuleCase {

    private final ModulePersistence modulePersistence;
    private final LessonPersistence lessonPersistence;
    private final BucketGateway bucketGateway;

    public AddLessonToModuleCase(ModulePersistence modulePersistence, LessonPersistence lessonPersistence, BucketGateway bucketGateway) {
        this.modulePersistence = modulePersistence;
        this.lessonPersistence = lessonPersistence;
        this.bucketGateway = bucketGateway;
    }

    public Lesson execute(String traceId, UUID moduleId, CreateLessonDTO dto, MultipartFile video) {
        log.debug("[{}] - Executing AddLessonToModuleCase for module ID {}", traceId, moduleId);
        Module module = this.modulePersistence.findById(moduleId)
                .orElseThrow(() -> {
                    log.error("[{}] - Module with ID {} not found", traceId, moduleId);
                    return new EntityNotFoundException("Module not found");
                });

        Integer position = module.getLessons() != null ? module.getLessons().size() + 1 : 1;
        log.debug("[{}] - Calculated position for new lesson: {}", traceId, position);

        String s3Key = uploadVideoToBucket(traceId, video);
        log.debug("[{}] - Video uploaded successfully with key: {}", traceId, s3Key);

        Lesson lesson = Lesson.builder()
                .title(dto.title())
                .position(position)
                .contentUrl(s3Key)
                .durationInSeconds(VideoUtils.getVideoDurationInSeconds(video))
                .module(module)
                .build();
        Lesson lessonSaved = this.lessonPersistence.save(lesson);
        log.debug("[{}] - Lesson saved with ID {}", traceId, lessonSaved.getId());

        return lessonSaved;
    }

    private String uploadVideoToBucket(String traceId, MultipartFile video) {
        String originalFilename = video.getOriginalFilename();
        String extension = ".mp4";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String s3Key = UUID.randomUUID() + extension;
        log.debug("[{}] - Uploading video to bucket with key: {}", traceId, s3Key);
        this.bucketGateway.uploadFile(s3Key, video);
        return s3Key;
    }

}
