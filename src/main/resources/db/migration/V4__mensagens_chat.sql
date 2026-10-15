-- =====================================================
-- 1) mensagens_chat: conversa entre cliente e beneficiária (sem IA)
-- =====================================================
CREATE TABLE mensagens_chat
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT         NOT NULL,
    autor_id   INT         NULL,
    remetente  VARCHAR(10) NOT NULL,
    conteudo   TEXT        NOT NULL,
    criado_em  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_mensagem_remetente CHECK (remetente IN ('CLIENTE', 'ADMIN')),

    CONSTRAINT fk_mensagem_cliente
        FOREIGN KEY (cliente_id)
            REFERENCES usuario (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,

    CONSTRAINT fk_mensagem_autor
        FOREIGN KEY (autor_id)
            REFERENCES usuario (id)
            ON DELETE SET NULL
            ON UPDATE CASCADE,

    INDEX idx_mensagem_conversa (cliente_id, id)
);

-- =====================================================
-- 2) chatbot_logs: só guarda a conversa com a IA (Instagram), origem não é mais necessária
-- =====================================================
ALTER TABLE chatbot_logs DROP CHECK chk_chatbot_origem;

ALTER TABLE chatbot_logs DROP COLUMN origem;