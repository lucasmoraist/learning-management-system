package com.lucasmoraist.lms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Certificate {

    private UUID id;
    private String title;
    private String description;
    private LocalDateTime issuedAt;
    private Profile profile;

}
