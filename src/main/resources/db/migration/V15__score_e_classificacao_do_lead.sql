-- =====================================================
-- 1) lead_instagram: score e classificacao vindos do CRM
-- =====================================================
ALTER TABLE lead_instagram
    ADD COLUMN score         INT         NULL,
    ADD COLUMN classificacao VARCHAR(20) NULL;

-- =====================================================
-- 2) etapa_funil: Apresentacao -> Orcamento Enviado
-- =====================================================
ALTER TABLE usuario DROP CHECK chk_usuario_etapa_funil;

UPDATE usuario
SET etapa_funil = 'Orcamento Enviado'
WHERE etapa_funil = 'Apresentacao';

ALTER TABLE usuario
    ADD CONSTRAINT chk_usuario_etapa_funil
        CHECK (etapa_funil IN ('Novo Lead', 'Em Qualificacao', 'Qualificado',
                               'Orcamento Enviado', 'Em Negociacao', 'Fechado'));