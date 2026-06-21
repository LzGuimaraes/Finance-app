import api from './api'

// GET /api/ativos -> AtivoResponse[]
export async function listar() {
  const { data } = await api.get('/api/ativos')
  return data
}

// GET /api/ativos/{id} -> AtivoResponse
export async function buscarPorId(id) {
  const { data } = await api.get(`/api/ativos/${id}`)
  return data
}

// GET /api/ativos/buscar?ticker=... -> AtivoResponse
export async function buscarPorTicker(ticker) {
  const { data } = await api.get('/api/ativos/buscar', { params: { ticker } })
  return data
}

// POST /api/ativos -> AtivoResponse (somente ADMIN)
export async function criar({ ticker, nome, tipo, cotacaoAtual }) {
  const { data } = await api.post('/api/ativos', { ticker, nome, tipo, cotacaoAtual })
  return data
}

// PUT /api/ativos/{id} -> AtivoResponse (somente ADMIN)
export async function atualizar(id, { ticker, nome, tipo, cotacaoAtual }) {
  const { data } = await api.put(`/api/ativos/${id}`, { ticker, nome, tipo, cotacaoAtual })
  return data
}

// PATCH /api/ativos/{id}/cotacao -> AtivoResponse (somente ADMIN)
export async function atualizarCotacao(id, cotacaoAtual) {
  const { data } = await api.patch(`/api/ativos/${id}/cotacao`, { cotacaoAtual })
  return data
}

// DELETE /api/ativos/{id} (somente ADMIN)
export async function deletar(id) {
  await api.delete(`/api/ativos/${id}`)
}