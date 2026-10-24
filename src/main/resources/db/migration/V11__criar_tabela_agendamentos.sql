CREATE TABLE agendamentos
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id       INT          NOT NULL,
    titulo           VARCHAR(150) NOT NULL,
    descricao        TEXT,
    inicio           DATETIME     NOT NULL,
    fim              DATETIME     NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDENTE',
    google_event_id  VARCHAR(255) NULL,
    observacao_admin TEXT NULL,
    criado_em        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_agendamentos_status
        CHECK (status IN ('PENDENTE', 'APROVADO', 'RECUSADO')),

    CONSTRAINT chk_agendamentos_periodo
        CHECK (fim > inicio),

    CONSTRAINT fk_agendamentos_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuario (id)
            ON DELETE RESTRICT
            ON UPDATE CASCADE,

    CONSTRAINT uk_agendamentos_google_event
        UNIQUE (google_event_id)
);
