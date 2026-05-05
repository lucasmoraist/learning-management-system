-- 4. Tabela de Perfil (Informações Bio/Sociais)
-- Nota: Relação 1:1 com Identity
CREATE TABLE tb_profile (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    identity_id UUID NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    bio TEXT,
    birth_date DATE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_profile_identity FOREIGN KEY (identity_id) REFERENCES tb_identity(id) ON DELETE CASCADE
);