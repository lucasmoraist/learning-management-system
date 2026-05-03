package com.lucasmoraist.lms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    private UUID id;
    private String name;
    private String bio;
    private LocalDate birthDate;
    private Identity identity;
    private List<Certificate> certificates;
    private List<Document> documents;
    private List<SocialLink> socialLinks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
