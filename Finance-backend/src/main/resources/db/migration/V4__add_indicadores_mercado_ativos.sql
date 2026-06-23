-- ==========================================
-- V4 - Indicadores de mercado adicionais em ativos
-- ==========================================
-- Campos retornados pela API do brapi.dev no momento da sincronização
-- diária de cotações. "Preço atual" já existe como cotacao_atual e
-- "Nome da empresa" já existe como nome (ambos desde V1), por isso
-- não são duplicados aqui.

ALTER TABLE ativos
    ADD COLUMN IF NOT EXISTS maxima_dia          DECIMAL(15, 6),
    ADD COLUMN IF NOT EXISTS minima_dia           DECIMAL(15, 6),
    ADD COLUMN IF NOT EXISTS volume_negociado     DECIMAL(18, 2),
    ADD COLUMN IF NOT EXISTS valor_mercado        DECIMAL(20, 2),
    ADD COLUMN IF NOT EXISTS pl                   DECIMAL(10, 4),
    ADD COLUMN IF NOT EXISTS maxima_52_semanas    DECIMAL(15, 6),
    ADD COLUMN IF NOT EXISTS minima_52_semanas    DECIMAL(15, 6);

-- Constraints de sanidade (todos os valores, quando presentes, são não-negativos;
-- P/L pode ser negativo em empresas com lucro negativo, então fica sem CHECK)
ALTER TABLE ativos
    ADD CONSTRAINT chk_maxima_dia_positiva       CHECK (maxima_dia IS NULL OR maxima_dia >= 0),
    ADD CONSTRAINT chk_minima_dia_positiva       CHECK (minima_dia IS NULL OR minima_dia >= 0),
    ADD CONSTRAINT chk_volume_negociado_positivo CHECK (volume_negociado IS NULL OR volume_negociado >= 0),
    ADD CONSTRAINT chk_valor_mercado_positivo    CHECK (valor_mercado IS NULL OR valor_mercado >= 0),
    ADD CONSTRAINT chk_maxima_52_positiva        CHECK (maxima_52_semanas IS NULL OR maxima_52_semanas >= 0),
    ADD CONSTRAINT chk_minima_52_positiva        CHECK (minima_52_semanas IS NULL OR minima_52_semanas >= 0);