-- =====================================================
-- convite_admin: convites para novos administradores
-- =====================================================
CREATE TABLE convite_admin
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    empresa_id    INT          NOT NULL,
    nome_completo VARCHAR(200) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    cpf           VARCHAR(20)  NOT NULL,
    token_hash    VARCHAR(64)  NOT NULL,
    status        VARCHAR(10)  NOT NULL DEFAULT 'PENDENTE',
    expira_em     TIMESTAMP    NOT NULL,
    criado_em     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    respondido_em TIMESTAMP    NULL,
    convidado_por INT          NULL,
    usuario_id    INT          NULL,

    CONSTRAINT uk_convite_token UNIQUE (token_hash),

    CONSTRAINT chk_convite_status
        CHECK (status IN ('PENDENTE', 'ACEITO', 'RECUSADO', 'REVOGADO')),

    CONSTRAINT fk_convite_empresa
        FOREIGN KEY (empresa_id)
            REFERENCES empresa (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,

    CONSTRAINT fk_convite_convidado_por
        FOREIGN KEY (convidado_por)
            REFERENCES usuario (id)
            ON DELETE SET NULL
            ON UPDATE CASCADE,

    CONSTRAINT fk_convite_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuario (id)
            ON DELETE SET NULL
            ON UPDATE CASCADE,

    INDEX idx_convite_empresa_status (empresa_id, status)
);