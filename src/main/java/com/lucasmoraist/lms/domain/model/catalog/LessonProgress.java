package com.lucasmoraist.lms.domain.model.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgress {

    private UUID id;
    private UUID profileId;
    private UUID lessonId;
    private Integer lastWatchedTimeInSeconds;
    private Boolean completed;
    private LocalDateTime updatedAt;

    public void updateProgress(Integer seconds, Integer totalDuration) {
        this.lastWatchedTimeInSeconds = seconds;
        this.updatedAt = LocalDateTime.now();

        if (totalDuration > 0 && ((double) seconds / totalDuration) >= 0.95) {
            this.completed = true;
        }
    }

}
