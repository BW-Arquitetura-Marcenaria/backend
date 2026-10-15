-- =====================================================
-- 1) usuario: perfil de acesso (CLIENTE ou ADMIN)
-- =====================================================
ALTER TABLE usuario
    ADD COLUMN perfil VARCHAR(20) NOT NULL DEFAULT 'CLIENTE',
    ADD CONSTRAINT chk_usuario_perfil CHECK (perfil IN ('CLIENTE', 'ADMIN'));