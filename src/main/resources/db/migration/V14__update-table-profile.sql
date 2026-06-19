-- 1. Cria a coluna permitindo valores nulos temporariamente
ALTER TABLE tb_profile ADD COLUMN subscription_id UUID UNIQUE;

-- [OPCIONAL] Associe ou crie assinaturas existentes para os perfis atuais aqui através de um UPDATE

-- 2. Torna a coluna obrigatória após popular os dados
ALTER TABLE tb_profile ALTER COLUMN subscription_id SET NOT NULL;

-- 3. Adiciona a chave estrangeira
ALTER TABLE tb_profile
ADD CONSTRAINT fk_profile_subscription
FOREIGN KEY (subscription_id) REFERENCES tb_subscription(id) ON DELETE CASCADE;