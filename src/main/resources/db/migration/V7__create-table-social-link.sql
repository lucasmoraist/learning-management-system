-- 6. Tabela de Links Sociais
CREATE TABLE tb_social_link (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    profile_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL, -- Ex: LinkedIn, GitHub
    link VARCHAR(255) NOT NULL,
    CONSTRAINT fk_social_profile FOREIGN KEY (profile_id) REFERENCES tb_profile(id) ON DELETE CASCADE
);