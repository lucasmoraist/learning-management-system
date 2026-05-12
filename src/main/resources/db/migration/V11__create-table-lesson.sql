CREATE TABLE tb_lesson (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    module_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    content_url TEXT, -- Link para S3/Vimeo/Youtube
    'position' INT NOT NULL, -- Para ordenar as aulas dentro do módulo
    duration_in_seconds INT,
    CONSTRAINT fk_lesson_module FOREIGN KEY (module_id) REFERENCES tb_module(id) ON DELETE CASCADE
);