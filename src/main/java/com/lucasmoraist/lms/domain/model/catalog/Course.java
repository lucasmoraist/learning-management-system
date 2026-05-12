package com.lucasmoraist.lms.domain.model.catalog;

import com.lucasmoraist.lms.domain.model.user.Identity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    private UUID id;
    private String title;
    private String description;
    private Identity instructor;
    private List<Module> modules;
    private LocalDateTime createdAt;

}
