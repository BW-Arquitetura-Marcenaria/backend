-- =====================================================
-- Tabela estado_oauth_google
-- =====================================================
CREATE TABLE estado_oauth_google
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    hash_estado            CHAR(64)     NOT NULL,
    hash_vinculo_navegador CHAR(64)     NOT NULL,
    data_expiracao         TIMESTAMP(6) NOT NULL,
    data_consumo           TIMESTAMP(6),
    data_criacao           TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT uk_estado_oauth_google_hash
        UNIQUE (hash_estado),

    INDEX  idx_estado_oauth_google_expiracao (data_expiracao)
);