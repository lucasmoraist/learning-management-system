CREATE TABLE tb_lesson_progress (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    profile_id UUID NOT NULL,
    lesson_id UUID NOT NULL,
    last_watched_time_in_seconds INT NOT NULL DEFAULT 0,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_tb_lesson_progress PRIMARY KEY (id),
    CONSTRAINT fk_lesson_progress_profile FOREIGN KEY (profile_id) REFERENCES tb_profile(id) ON DELETE CASCADE,
    CONSTRAINT fk_lesson_progress_lesson FOREIGN KEY (lesson_id) REFERENCES tb_lesson(id) ON DELETE CASCADE,
    CONSTRAINT uq_profile_lesson UNIQUE (profile_id, lesson_id)
);

CREATE INDEX idx_lesson_progress_lookup ON tb_lesson_progress(profile_id, lesson_id);