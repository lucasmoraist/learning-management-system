package com.lucasmoraist.lms.domain.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference
    private Identity identity;
    @JsonManagedReference
    private List<Certificate> certificates;
    @JsonManagedReference
    private List<Document> documents;
    @JsonManagedReference
    private List<SocialLink> socialLinks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
