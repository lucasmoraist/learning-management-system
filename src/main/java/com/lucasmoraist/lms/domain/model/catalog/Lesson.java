package com.lucasmoraist.lms.domain.model.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
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
