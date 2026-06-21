-- ==========================================
-- V2 - Campos de autenticação e refresh tokens
-- ==========================================

-- Adicionar campos de confirmação de e-mail e reset de senha na tabela usuarios
ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS email_confirmado     BOOLEAN DEFAULT FALSE NOT NULL,
    ADD COLUMN IF NOT EXISTS token_confirmacao_email VARCHAR(255),
    ADD COLUMN IF NOT EXISTS token_reset_senha    VARCHAR(255),
    ADD COLUMN IF NOT EXISTS expiracao_token_reset TIMESTAMP WITH TIME ZONE;

-- Índices para lookup rápido dos tokens
CREATE INDEX IF NOT EXISTS idx_usuarios_token_confirmacao ON usuarios(token_confirmacao_email) WHERE token_confirmacao_email IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_usuarios_token_reset       ON usuarios(token_reset_senha)       WHERE token_reset_senha IS NOT NULL;

-- ==========================================
-- Tabela de Refresh Tokens
-- ==========================================
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token       VARCHAR(512) UNIQUE NOT NULL,
    usuario_id  UUID NOT NULL,
    expiracao   TIMESTAMP WITH TIME ZONE NOT NULL,
    revogado    BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT fk_refresh_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_usuario  ON refresh_tokens(usuario_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token    ON refresh_tokens(token);