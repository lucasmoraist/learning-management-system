-- 7. Tabela de Certificados
CREATE TABLE tb_certificate (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    profile_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    issuedAt TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_certificate_profile FOREIGN KEY (profile_id) REFERENCES tb_profile(id) ON DELETE CASCADE
);