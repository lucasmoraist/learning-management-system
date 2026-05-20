package com.lucasmoraist.lms.application.usecases.course;

import com.lucasmoraist.lms.adapter.web.dto.course.CreateCourseDTO;
import com.lucasmoraist.lms.application.utils.HeaderUtils;
import com.lucasmoraist.lms.domain.exceptions.AuthenticationException;
import com.lucasmoraist.lms.domain.gateway.TokenGateway;
import com.lucasmoraist.lms.domain.model.catalog.Course;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.infrastructure.database.persistence.CoursePersistence;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class CreateCourseCase {

    private final TokenGateway tokenGateway;
    private final CoursePersistence coursePersistence;
    private final IdentityPersistence identityPersistence;

    public CreateCourseCase(TokenGateway tokenGateway, CoursePersistence coursePersistence, IdentityPersistence identityPersistence) {
        this.tokenGateway = tokenGateway;
        this.coursePersistence = coursePersistence;
        this.identityPersistence = identityPersistence;
    }

    public Course execute(String traceId, String authorizationHeader, CreateCourseDTO dto) {
        log.debug("[{}] - Creating course", traceId);

        final String token = HeaderUtils.getBearerToken(authorizationHeader);
        final UUID instructorId = UUID.fromString(this.tokenGateway.getSubjectFromToken(token));

        Identity identity = this.identityPersistence.findById(instructorId)
                .orElseThrow(() -> {
                    log.error("[{}] - Identity with id {} not found", traceId, instructorId);
                    return new AuthenticationException("Identity not found");
                });

        Course course = Course.builder()
                .title(dto.title())
                .description(dto.description())
                .instructor(identity)
                .build();

        Course courseSaved = this.coursePersistence.save(course);
        log.debug("[{}] - Course with id {} created successfully", traceId, courseSaved.getId());
        return courseSaved;
    }

}
