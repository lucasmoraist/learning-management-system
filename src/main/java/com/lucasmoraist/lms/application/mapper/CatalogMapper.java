package com.lucasmoraist.lms.application.mapper;

import com.lucasmoraist.lms.domain.model.catalog.Course;
import com.lucasmoraist.lms.domain.model.catalog.Lesson;
import com.lucasmoraist.lms.domain.model.catalog.Module;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class CatalogMapper {

    public static Map<String, Object> mapCourseToResponse(Course course) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", course.getId());
        response.put("title", course.getTitle());
        response.put("description", course.getDescription());

        Map<String, Object> instructor = new LinkedHashMap<>();
        instructor.put("id", course.getInstructor().getId());
        instructor.put("name", course.getInstructor().getProfile().getName());
        instructor.put("email", course.getInstructor().getEmail());
        response.put("instructor", instructor);

        List<Object> modules = course.getModules().isEmpty()
                ? new ArrayList<>()
                : course.getModules().stream()
                .map(CatalogMapper::mapModuleToResponse)
                .collect(Collectors.toList());
        response.put("modules", modules);

        response.put("createdAt", course.getUpdatedAt());
        return response;
    }

    public static Map<String, Object> mapModuleToResponse(Module module) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", module.getId());
        response.put("title", module.getTitle());
        response.put("position", module.getPosition());

        List<Object> lessons = module.getLessons().isEmpty()
                ? new ArrayList<>()
                : module.getLessons().stream()
                .map(CatalogMapper::mapLessonsToResponse)
                .collect(Collectors.toList());
        response.put("lessons", lessons);

        return response;
    }

    public static Map<String, Object> mapLessonsToResponse(Lesson lesson) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", lesson.getId());
        response.put("title", lesson.getTitle());
        response.put("contentUrl", lesson.getContentUrl());
        response.put("position", lesson.getPosition());
        response.put("durationInSeconds", lesson.getDurationInSeconds());
        return response;
    }


}
