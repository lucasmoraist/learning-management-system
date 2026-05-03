-- 3. Tabela de Junção (Many-to-Many entre Identity e Role)
CREATE TABLE tb_identity_role (
    identity_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (identity_id, role_id),
    CONSTRAINT fk_identity FOREIGN KEY (identity_id) REFERENCES tb_identity(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE
);