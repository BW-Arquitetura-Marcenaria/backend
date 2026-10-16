-- =====================================================
-- 1) empresa: telefone comercial
-- =====================================================
ALTER TABLE empresa
    ADD COLUMN telefone VARCHAR(20) NULL;

-- Empresas já existentes herdam o telefone do representante
UPDATE empresa e
    JOIN usuario u ON u.empresa_id = e.id
    SET e.telefone = u.telefone;

-- =====================================================
-- 2) usuario: telefone passa a ser opcional (na PJ fica na empresa)
-- =====================================================
ALTER TABLE usuario MODIFY COLUMN telefone VARCHAR(20) NULL;