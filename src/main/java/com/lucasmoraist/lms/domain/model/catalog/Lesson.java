package com.lucasmoraist.lms.domain.model.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {

    private UUID id;
    private String title;
    private String contentUrl;
    private Integer position;
    private Integer durationInSeconds;
    private Module module;

}
