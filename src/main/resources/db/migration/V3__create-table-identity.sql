-- 2. Tabela de Identidade (Credenciais e Segurança)
CREATE TABLE tb_identity (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email TEXT NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE
);