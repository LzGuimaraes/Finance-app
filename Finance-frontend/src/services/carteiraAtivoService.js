import api from './api'

// GET /api/carteira -> CarteiraAtivoResponse[]
// Sempre a carteira do usuário autenticado.
export async function listar() {
  const { data } = await api.get('/api/carteira')
  return data
}

// GET /api/carteira/{id} -> CarteiraAtivoResponse
export async function buscarPorId(id) {
  const { data } = await api.get(`/api/carteira/${id}`)
  return data
}

// POST /api/carteira -> CarteiraAtivoResponse
export async function criar({ ativoId, quantidade, precoMedio, notaQualidade }) {
  const { data } = await api.post('/api/carteira', { ativoId, quantidade, precoMedio, notaQualidade })
  return data
}

// PUT /api/carteira/{id} -> CarteiraAtivoResponse
// Não permite troca de ativo (ativoId precisa ser o mesmo da posição existente).
export async function atualizar(id, { ativoId, quantidade, precoMedio, notaQualidade }) {
  const { data } = await api.put(`/api/carteira/${id}`, { ativoId, quantidade, precoMedio, notaQualidade })
  return data
}

// DELETE /api/carteira/{id}
export async function deletar(id) {
  await api.delete(`/api/carteira/${id}`)
}