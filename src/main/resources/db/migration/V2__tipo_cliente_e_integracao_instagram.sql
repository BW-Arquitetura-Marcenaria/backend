-- =====================================================
-- 1) tipo_cliente: Empresa -> Pessoa Juridica
-- =====================================================
ALTER TABLE usuario DROP CHECK chk_usuario_tipo_cliente;

UPDATE usuario SET tipo_cliente = 'Pessoa Juridica' WHERE tipo_cliente = 'Empresa';

ALTER TABLE usuario
    ADD CONSTRAINT chk_usuario_tipo_cliente
        CHECK (tipo_cliente IN ('Pessoa Fisica', 'Pessoa Juridica'));

-- =====================================================
-- 2) usuario: Instagram, login e unicidade do e-mail
-- =====================================================
ALTER TABLE usuario
    ADD COLUMN instagram_id VARCHAR(40) NULL,
    ADD COLUMN senha_hash   VARCHAR(100) NULL,
    ADD COLUMN ativo        BOOLEAN NOT NULL DEFAULT TRUE,
    ADD UNIQUE KEY uk_usuario_instagram (instagram_id),
    ADD UNIQUE KEY uk_usuario_email (email);

-- =====================================================
-- 3) lead_instagram: lead capturado pelo agente (antes do cadastro)
-- =====================================================
CREATE TABLE lead_instagram
(
    id                     INT AUTO_INCREMENT PRIMARY KEY,
    instagram_id           VARCHAR(40)  NOT NULL,
    crm_lead_id            VARCHAR(30),
    nome                   VARCHAR(100),
    sao_paulo              BOOLEAN   DEFAULT FALSE,
    ambiente               VARCHAR(60),
    movel                  VARCHAR(80),
    cor                    VARCHAR(40),
    material               VARCHAR(40),
    orcamento              DECIMAL(10, 2),
    situacao_projeto       VARCHAR(40),
    precisa_visita_tecnica BOOLEAN   DEFAULT FALSE,
    resumo_conversa        TEXT,
    token_hash             CHAR(64),
    token_expira_em        DATETIME,
    token_usado_em         DATETIME NULL,
    link_enviado_em        DATETIME NULL,
    usuario_id             INT NULL,
    criado_em              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_lead_instagram UNIQUE (instagram_id),
    CONSTRAINT uk_lead_token UNIQUE (token_hash),

    CONSTRAINT fk_lead_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuario (id)
            ON DELETE SET NULL
            ON UPDATE CASCADE
);

-- =====================================================
-- 4) chatbot_logs: conversa de quem ainda não é usuário
-- =====================================================
ALTER TABLE chatbot_logs
    ADD COLUMN instagram_id VARCHAR(40) NULL,
    ADD COLUMN origem VARCHAR(20) NOT NULL DEFAULT 'INSTAGRAM',
    ADD CONSTRAINT chk_chatbot_origem CHECK (origem IN ('INSTAGRAM', 'APP')),
    ADD INDEX idx_chat_instagram (instagram_id);