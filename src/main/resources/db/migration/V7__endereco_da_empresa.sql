-- =====================================================
-- endereco: endereço comercial da empresa (um por empresa)
-- =====================================================
CREATE TABLE endereco
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    empresa_id  INT          NOT NULL,
    cep         VARCHAR(8)   NOT NULL,
    logradouro  VARCHAR(150) NOT NULL,
    numero      VARCHAR(20)  NOT NULL,
    complemento VARCHAR(100) NULL,
    bairro      VARCHAR(100) NOT NULL,
    cidade      VARCHAR(100) NOT NULL,
    uf          CHAR(2)      NOT NULL,

    CONSTRAINT uk_endereco_empresa UNIQUE (empresa_id),

    CONSTRAINT fk_endereco_empresa
        FOREIGN KEY (empresa_id)
            REFERENCES empresa (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);