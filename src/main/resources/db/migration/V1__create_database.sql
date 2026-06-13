-- Habilitar extensão para UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ==========================================
-- 1. CONTROLE DE ACESSO E USUÁRIOS
-- ==========================================

CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'usuario_comum' CHECK (role IN ('admin', 'usuario_comum')),
    data_criacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 2. CATEGORIZAÇÃO DE PATRIMÔNIO GLOBAL
-- ==========================================

CREATE TABLE categorias_investimento (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE subcategorias_investimento (
    id SERIAL PRIMARY KEY,
    categoria_id INT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    CONSTRAINT fk_categoria FOREIGN KEY (categoria_id) REFERENCES categorias_investimento(id) ON DELETE CASCADE,
    UNIQUE (categoria_id, nome)
);

CREATE TABLE metas_alocacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL,
    subcategoria_id INT NOT NULL,
    percentual_meta DECIMAL(5, 2) NOT NULL CHECK (percentual_meta >= 0 AND percentual_meta <= 100),
    CONSTRAINT fk_usuario_meta FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_subcategoria_meta FOREIGN KEY (subcategoria_id) REFERENCES subcategorias_investimento(id) ON DELETE RESTRICT,
    UNIQUE (usuario_id, subcategoria_id) -- Um usuário só pode ter uma meta por subcategoria
);

-- ==========================================
-- 3. ATIVOS E CARTEIRA (COM RATING)
-- ==========================================

CREATE TABLE ativos (
    id SERIAL PRIMARY KEY,
    ticker VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    cotacao_atual DECIMAL(15, 6) DEFAULT 0 CHECK (cotacao_atual >= 0),
    data_atualizacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE carteira_ativos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL,
    ativo_id INT NOT NULL,
    quantidade DECIMAL(15, 6) NOT NULL DEFAULT 0 CHECK (quantidade >= 0),
    preco_medio DECIMAL(15, 6) NOT NULL DEFAULT 0 CHECK (preco_medio >= 0),
    nota_qualidade INT CHECK (nota_qualidade >= 0 AND nota_qualidade <= 10), -- Requisito Especial: Nota 0 a 10
    CONSTRAINT fk_usuario_carteira FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_ativo_carteira FOREIGN KEY (ativo_id) REFERENCES ativos(id) ON DELETE RESTRICT,
    UNIQUE (usuario_id, ativo_id) -- Impede que o usuário tenha o mesmo ativo duplicado na carteira
);

-- ==========================================
-- 4. HISTÓRICO DE EVOLUÇÃO PATRIMONIAL
-- ==========================================

CREATE TABLE historico_saldos_mensais (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL,
    subcategoria_id INT NOT NULL,
    data_referencia DATE NOT NULL, -- Ex: '2023-10-01' para representar Outubro/2023
    saldo_consolidado DECIMAL(15, 2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_usuario_historico FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_subcategoria_historico FOREIGN KEY (subcategoria_id) REFERENCES subcategorias_investimento(id) ON DELETE RESTRICT,
    UNIQUE (usuario_id, subcategoria_id, data_referencia)
);

-- ==========================================
-- 5. MÓDULO DE GASTOS E FLUXO DE CAIXA
-- ==========================================

CREATE TABLE categorias_gastos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE transacoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL,
    categoria_gasto_id INT NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    valor DECIMAL(15, 2) NOT NULL CHECK (valor > 0), -- Valores sempre positivos, o tipo define se soma ou subtrai
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('receita', 'despesa')),
    data_transacao DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('pago', 'pendente')),
    CONSTRAINT fk_usuario_transacao FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_categoria_transacao FOREIGN KEY (categoria_gasto_id) REFERENCES categorias_gastos(id) ON DELETE RESTRICT
);

-- ==========================================
-- 6. ÍNDICES DE OTIMIZAÇÃO (PERFORMANCE)
-- ==========================================

-- Índices para chaves estrangeiras frequentemente utilizadas em filtros/joins
CREATE INDEX idx_metas_usuario ON metas_alocacao(usuario_id);
CREATE INDEX idx_carteira_usuario ON carteira_ativos(usuario_id);
CREATE INDEX idx_historico_usuario ON historico_saldos_mensais(usuario_id);
CREATE INDEX idx_transacoes_usuario ON transacoes(usuario_id);
CREATE INDEX idx_transacoes_data ON transacoes(data_transacao);
CREATE INDEX idx_ativos_ticker ON ativos(ticker);