-- 5. Tabela de Documentos (CPF, RG, etc)
CREATE TABLE tb_document (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    profile_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL, -- Ex: CPF, RG, CNPJ
    number VARCHAR(100) NOT NULL,
    CONSTRAINT fk_document_profile FOREIGN KEY (profile_id) REFERENCES tb_profile(id) ON DELETE CASCADE
);