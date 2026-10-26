-- =====================================================
-- Tabela financeiro
-- Tabela independente, sem relacionamento com outras tabelas.
-- =====================================================
CREATE TABLE financeiro
(
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    descricao               TEXT,
    quantidade              INT            NOT NULL,
    valor                   DECIMAL(10, 2) NOT NULL,
    frete                   DECIMAL(10, 2) NOT NULL DEFAULT 0,
    valor_total             DECIMAL(10, 2) NOT NULL,
    valor_final_desconto    DECIMAL(10, 2),
    valor_entrada           DECIMAL(10, 2),
    receber_entrega         DECIMAL(10, 2),
    valor_recebido_final    DECIMAL(10, 2),
    valor_total_liquido     DECIMAL(10, 2),
    data_pedido             DATE,
    data_entrega            DATE,
    contato_cliente         VARCHAR(100),
    regiao_venda            VARCHAR(100)
);
