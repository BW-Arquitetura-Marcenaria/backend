-- =====================================================
-- Tabelas de assinatura eletrônica de contratos (Clicksign)
-- =====================================================
CREATE TABLE assinaturas_clicksign
(
    id                INT AUTO_INCREMENT PRIMARY KEY,
    projeto_id        INT         NOT NULL,
    envelope_id       VARCHAR(60) NOT NULL,
    document_id       VARCHAR(60) NOT NULL,
    nome_arquivo      VARCHAR(150) NOT NULL,
    status            VARCHAR(20) NOT NULL DEFAULT 'RASCUNHO',
    data_envio        DATETIME,
    data_atualizacao  DATETIME,
    CONSTRAINT fk_assinatura_projeto FOREIGN KEY (projeto_id) REFERENCES projetos (id),
    CONSTRAINT uk_assinatura_envelope_id UNIQUE (envelope_id),
    CONSTRAINT uk_assinatura_document_id UNIQUE (document_id)
);

CREATE TABLE assinatura_signatarios
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    assinatura_id  INT          NOT NULL,
    signer_id      VARCHAR(60)  NOT NULL,
    nome           VARCHAR(150) NOT NULL,
    email          VARCHAR(150) NOT NULL,
    cpf            VARCHAR(20),
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDENTE',
    CONSTRAINT fk_signatario_assinatura FOREIGN KEY (assinatura_id) REFERENCES assinaturas_clicksign (id),
    CONSTRAINT uk_signatario_signer_id UNIQUE (signer_id)
);
