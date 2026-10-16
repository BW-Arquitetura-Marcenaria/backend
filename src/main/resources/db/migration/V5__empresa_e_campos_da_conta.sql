-- =====================================================
-- 1) empresa: dados da pessoa jurídica (o usuário PJ é o representante)
-- =====================================================
CREATE TABLE empresa
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    cnpj          VARCHAR(20)  NOT NULL,
    razao_social  VARCHAR(150) NOT NULL,
    nome_fantasia VARCHAR(150) NULL,
    data_cadastro TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_empresa_cnpj UNIQUE (cnpj)
);

-- Empresas já existentes (usuários com CNPJ); razão social provisória
INSERT INTO empresa (cnpj, razao_social)
SELECT cnpj, MIN(CONCAT(nome, ' ', sobrenome))
FROM usuario
WHERE cnpj IS NOT NULL
GROUP BY cnpj;

-- =====================================================
-- 2) usuario: campos da tela Minha Conta e vínculo com a empresa
-- =====================================================
ALTER TABLE usuario
    ADD COLUMN origem_contato     VARCHAR(30) NULL,
    ADD COLUMN instagram_username VARCHAR(60) NULL,
    ADD COLUMN empresa_id         INT         NULL,

    ADD CONSTRAINT fk_usuario_empresa
        FOREIGN KEY (empresa_id)
            REFERENCES empresa (id)
            ON DELETE SET NULL
            ON UPDATE CASCADE,

                                                                                       ADD INDEX idx_usuario_empresa (empresa_id);

UPDATE usuario u
    JOIN empresa e ON e.cnpj = u.cnpj
    SET u.empresa_id = e.id;

-- =====================================================
-- 3) usuario: o CNPJ agora vive só na empresa
-- =====================================================
ALTER TABLE usuario DROP COLUMN cnpj;
