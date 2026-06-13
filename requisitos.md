# Documento de Requisitos — Sistema de Gestão Patrimonial e Financeira

## 1. Visão Geral

O sistema tem como objetivo permitir que usuários realizem o gerenciamento completo de patrimônio financeiro, controle de investimentos, metas de alocação e fluxo de caixa pessoal.

A plataforma deverá possibilitar:

* Cadastro e autenticação de usuários
* Controle de carteira de ativos
* Definição de metas de alocação
* Acompanhamento da evolução patrimonial
* Controle de receitas e despesas
* Classificação de ativos por categorias
* Análise de qualidade de ativos

---

# 2. Requisitos Funcionais

## 2.1 Controle de Usuários

### RF001 — Cadastro de usuários

O sistema deverá permitir o cadastro de usuários contendo:

* Nome
* Email
* Senha criptografada
* Tipo de acesso

### RF002 — Autenticação

O sistema deverá permitir login utilizando email e senha.

### RF003 — Controle de permissões

O sistema deverá possuir dois níveis de acesso:

* Administrador
* Usuário comum

---

# 2.2 Categorias de Investimentos

### RF004 — Cadastro de categorias

O sistema deverá permitir o cadastro de categorias de investimento.

Exemplos:

* Renda fixa
* Renda variável
* Internacional
* Criptomoedas

### RF005 — Cadastro de subcategorias

O sistema deverá permitir vincular subcategorias às categorias principais.

Exemplos:

* Ações
* FIIs
* ETFs
* Tesouro Direto

---

# 2.3 Metas de Alocação

### RF006 — Definição de metas

O usuário poderá definir metas percentuais para cada subcategoria de investimento.

### RF007 — Validação de percentual

O sistema deverá aceitar apenas valores entre 0% e 100%.

### RF008 — Restrição de duplicidade

O usuário não poderá possuir mais de uma meta para a mesma subcategoria.

---

# 2.4 Gestão de Ativos

### RF009 — Cadastro de ativos

O sistema deverá permitir o cadastro de ativos financeiros contendo:

* Ticker
* Nome
* Tipo
* Cotação atual

### RF010 — Atualização de cotação

O sistema deverá registrar a data da última atualização de preço.

---

# 2.5 Carteira de Investimentos

### RF011 — Registro de ativos na carteira

O usuário poderá adicionar ativos à carteira.

### RF012 — Controle de quantidade

O sistema deverá armazenar quantidade de ativos e preço médio.

### RF013 — Avaliação qualitativa

O usuário poderá atribuir nota de qualidade entre 0 e 10 para cada ativo.

### RF014 — Restrição de duplicidade

Um usuário não poderá possuir o mesmo ativo duplicado na carteira.

---

# 2.6 Histórico Patrimonial

### RF015 — Histórico mensal

O sistema deverá registrar snapshots mensais do patrimônio consolidado.

### RF016 — Consolidação por subcategoria

Os saldos deverão ser armazenados por subcategoria de investimento.

### RF017 — Controle temporal

O sistema deverá impedir duplicidade de registros mensais.

---

# 2.7 Controle Financeiro

### RF018 — Cadastro de categorias financeiras

O sistema deverá permitir cadastrar categorias de gastos.

Exemplos:

* Alimentação
* Transporte
* Moradia
* Salário

### RF019 — Registro de transações

O usuário poderá registrar receitas e despesas.

### RF020 — Controle de status

As transações deverão possuir status:

* Pago
* Pendente

### RF021 — Classificação financeira

As transações deverão possuir:

* Descrição
* Valor
* Tipo
* Data

---

# 3. Requisitos Não Funcionais

## RNF001 — Segurança

As senhas deverão ser armazenadas utilizando hash criptográfico seguro.

## RNF002 — Integridade relacional

O banco deverá utilizar chaves estrangeiras para garantir consistência dos dados.

## RNF003 — Performance

O sistema deverá possuir índices para otimização de consultas.

## RNF004 — Escalabilidade

O sistema deverá suportar expansão futura de módulos financeiros.

## RNF005 — Persistência

O sistema deverá utilizar PostgreSQL como banco principal.

---

# 4. Regras de Negócio

## RN001

O percentual de meta deverá permanecer entre 0% e 100%.

## RN002

O valor de transações deverá ser sempre positivo.

## RN003

O tipo da transação definirá se o valor será receita ou despesa.

## RN004

Cada usuário poderá possuir apenas um registro por ativo na carteira.

## RN005

Cada usuário poderá possuir apenas uma meta por subcategoria.

## RN006

As notas de qualidade dos ativos deverão variar entre 0 e 10.

---

# 5. Estrutura Modular

O sistema será dividido nos seguintes módulos:

1. Autenticação
2. Gestão de usuários
3. Investimentos
4. Carteira patrimonial
5. Metas de alocação
6. Histórico patrimonial
7. Fluxo de caixa
8. Dashboard e indicadores

---

# 6. Possíveis Funcionalidades Futuras

* Integração com APIs de mercado financeiro
* Atualização automática de cotações
* Dashboard com gráficos
* Simulação de rebalanceamento
* Alertas de desvio de alocação
* Relatórios em PDF
* Exportação Excel
* Multi-moeda
* Controle tributário
* App mobile

---

# 7. Tecnologias Sugeridas

## Backend

* Java + Spring Boot

## Frontend

* React
* Next.js

## Banco de Dados

* PostgreSQL

## Infraestrutura

* Docker
* AWS
* Vercel

---

# 8. Objetivo Estratégico

O sistema deverá fornecer ao usuário uma visão consolidada do patrimônio financeiro, auxiliando no acompanhamento de crescimento patrimonial, organização financeira e tomada de decisão de investimentos.
