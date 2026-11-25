-- O lead deixa de depender do Instagram: aceita convite por e-mail e cliente da plataforma
ALTER TABLE lead_instagram
    MODIFY COLUMN instagram_id VARCHAR(40) NULL,
    ADD COLUMN origem VARCHAR(20) NOT NULL DEFAULT 'Instagram' AFTER instagram_id,
    ADD COLUMN email VARCHAR(150) NULL AFTER nome,
    ADD CONSTRAINT uk_lead_email UNIQUE (email),
    ADD CONSTRAINT uk_lead_usuario UNIQUE (usuario_id),
    ADD CONSTRAINT chk_lead_origem CHECK (origem IN ('Instagram', 'Plataforma')),
    ADD CONSTRAINT chk_lead_identificacao CHECK (instagram_id IS NOT NULL OR email IS NOT NULL);

-- Cliente já cadastrado sem etapa passa a começar no Novo Lead
UPDATE usuario
SET etapa_funil = 'Novo Lead'
WHERE perfil = 'CLIENTE' AND etapa_funil IS NULL;