package com.lucasmoraist.lms.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLink {

    private UUID id;
    private String name;
    private String link;
    @JsonBackReference
    private Profile profile;

}
