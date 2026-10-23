-- =====================================================
-- Tabela credenciais_google_calendar
-- =====================================================
CREATE TABLE credenciais_google_calendar
(
    id                              BIGINT AUTO_INCREMENT PRIMARY KEY,
    token_atualizacao_criptografado TEXT         NOT NULL,
    escopo                          VARCHAR(500) NOT NULL,
    data_criacao                    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    data_atualizacao                TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);
