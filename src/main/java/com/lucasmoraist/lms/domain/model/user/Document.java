package com.lucasmoraist.lms.domain.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lucasmoraist.lms.domain.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    private UUID id;
    private DocumentType type;
    private String number;
    @JsonBackReference
    private Profile profile;

}
