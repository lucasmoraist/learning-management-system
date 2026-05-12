package com.lucasmoraist.lms.domain.model.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Module {

    private UUID id;
    private String title;
    private Integer position;
    private Course course;
    private List<Lesson> lessons;

}
