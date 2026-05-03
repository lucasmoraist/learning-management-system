package com.lucasmoraist.lms.infrastructure.database.entity;

import com.lucasmoraist.lms.domain.enums.DocumentType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_document")
@Entity(name = "tb_document")
public class DocumentEntity {

    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    private DocumentType type;
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

}
