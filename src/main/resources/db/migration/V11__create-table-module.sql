CREATE TABLE tb_module (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    course_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    'position' INT NOT NULL, -- Para ordenar os módulos
    CONSTRAINT fk_module_course FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE
);