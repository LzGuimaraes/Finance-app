-- ==========================================
-- V3 - Seed inicial de categorias e subcategorias de investimento
-- ==========================================
-- Catálogo global (não pertence a usuário específico).
-- Usa ON CONFLICT para ser seguro em re-execuções (idempotente).

INSERT INTO categorias_investimento (nome) VALUES
    ('Renda Fixa'),
    ('Renda Variável'),
    ('Fundos Imobiliários'),
    ('Criptomoedas'),
    ('Reserva de Emergência'),
    ('Internacional')
ON CONFLICT (nome) DO NOTHING;

-- Renda Fixa
INSERT INTO subcategorias_investimento (categoria_id, nome)
SELECT c.id, s.nome
FROM categorias_investimento c
CROSS JOIN (VALUES
    ('Tesouro Selic'),
    ('Tesouro IPCA+'),
    ('Tesouro Prefixado'),
    ('CDB'),
    ('LCI'),
    ('LCA'),
    ('Debêntures'),
    ('CRI/CRA')
) AS s(nome)
WHERE c.nome = 'Renda Fixa'
ON CONFLICT (categoria_id, nome) DO NOTHING;

-- Renda Variável
INSERT INTO subcategorias_investimento (categoria_id, nome)
SELECT c.id, s.nome
FROM categorias_investimento c
CROSS JOIN (VALUES
    ('Ações Brasil'),
    ('Ações Internacionais'),
    ('ETFs'),
    ('BDRs')
) AS s(nome)
WHERE c.nome = 'Renda Variável'
ON CONFLICT (categoria_id, nome) DO NOTHING;

-- Fundos Imobiliários
INSERT INTO subcategorias_investimento (categoria_id, nome)
SELECT c.id, s.nome
FROM categorias_investimento c
CROSS JOIN (VALUES
    ('FII de Papel'),
    ('FII de Tijolo'),
    ('FII Híbrido'),
    ('FII de Fundos (FOF)')
) AS s(nome)
WHERE c.nome = 'Fundos Imobiliários'
ON CONFLICT (categoria_id, nome) DO NOTHING;

-- Criptomoedas
INSERT INTO subcategorias_investimento (categoria_id, nome)
SELECT c.id, s.nome
FROM categorias_investimento c
CROSS JOIN (VALUES
    ('Bitcoin'),
    ('Ethereum'),
    ('Altcoins'),
    ('Stablecoins')
) AS s(nome)
WHERE c.nome = 'Criptomoedas'
ON CONFLICT (categoria_id, nome) DO NOTHING;

-- Reserva de Emergência
INSERT INTO subcategorias_investimento (categoria_id, nome)
SELECT c.id, s.nome
FROM categorias_investimento c
CROSS JOIN (VALUES
    ('Conta Corrente Remunerada'),
    ('CDB Liquidez Diária'),
    ('Tesouro Selic (Reserva)')
) AS s(nome)
WHERE c.nome = 'Reserva de Emergência'
ON CONFLICT (categoria_id, nome) DO NOTHING;

-- Internacional
INSERT INTO subcategorias_investimento (categoria_id, nome)
SELECT c.id, s.nome
FROM categorias_investimento c
CROSS JOIN (VALUES
    ('ETFs Internacionais'),
    ('Ações EUA'),
    ('Moeda Estrangeira (USD/EUR)')
) AS s(nome)
WHERE c.nome = 'Internacional'
ON CONFLICT (categoria_id, nome) DO NOTHING;