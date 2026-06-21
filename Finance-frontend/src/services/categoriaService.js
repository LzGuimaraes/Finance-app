import api from './api'

// GET /api/categorias -> CategoriaResponse[]
export async function listar() {
  const { data } = await api.get('/api/categorias')
  return data
}

// GET /api/categorias/{id} -> CategoriaResponse
export async function buscarPorId(id) {
  const { data } = await api.get(`/api/categorias/${id}`)
  return data
}

// POST /api/categorias -> CategoriaResponse (somente ADMIN)
export async function criar({ nome }) {
  const { data } = await api.post('/api/categorias', { nome })
  return data
}

// PUT /api/categorias/{id} -> CategoriaResponse (somente ADMIN)
export async function atualizar(id, { nome }) {
  const { data } = await api.put(`/api/categorias/${id}`, { nome })
  return data
}

// DELETE /api/categorias/{id} (somente ADMIN)
export async function deletar(id) {
  await api.delete(`/api/categorias/${id}`)
}