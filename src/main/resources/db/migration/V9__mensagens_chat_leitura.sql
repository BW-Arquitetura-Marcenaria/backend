-- =====================================================
-- mensagens_chat: controle de leitura (contador de nao lidas)
-- =====================================================
ALTER TABLE mensagens_chat
    ADD COLUMN lida_em TIMESTAMP NULL,
    ADD INDEX idx_mensagem_nao_lidas (cliente_id, remetente, lida_em);

-- =====================================================
-- mensagens que ja existiam contam como lidas
-- =====================================================
UPDATE mensagens_chat
SET lida_em = criado_em
WHERE lida_em IS NULL;