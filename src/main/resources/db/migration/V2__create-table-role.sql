-- 1. Tabela de Roles (Admin, Instrutor, Aluno)
CREATE TABLE tb_role (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL UNIQUE
);