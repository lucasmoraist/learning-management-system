package com.lucasmoraist.lms.infrastructure.database.entity.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tb_lesson_progress")
@Table(name = "tb_lesson_progress")
public class LessonProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID profileId;
    private UUID lessonId;
    private Integer lastWatchedTimeInSeconds;
    private Boolean completed;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
