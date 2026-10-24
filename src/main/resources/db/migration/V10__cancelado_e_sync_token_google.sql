-- Permite marcar um agendamento como CANCELADO quando o evento correspondente
-- é removido diretamente no Google Calendar (sincronização reversa).
ALTER TABLE agendamentos
    DROP CHECK chk_agendamentos_status;

ALTER TABLE agendamentos
    ADD CONSTRAINT chk_agendamentos_status
        CHECK (status IN ('PENDENTE', 'APROVADO', 'RECUSADO', 'CANCELADO'));

-- Guarda o syncToken incremental do Google Calendar para a sincronização
-- periódica (job agendado) conseguir buscar só o que mudou desde a última execução.
ALTER TABLE credenciais_google_calendar
    ADD COLUMN sync_token TEXT NULL AFTER escopo;
