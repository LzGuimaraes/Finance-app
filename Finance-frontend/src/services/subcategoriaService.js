import api from './api'

// GET /api/categorias/{categoriaId}/subcategorias -> SubcategoriaResponse[]
export async function listar(categoriaId) {
  const { data } = await api.get(`/api/categorias/${categoriaId}/subcategorias`)
  return data
}

// GET /api/categorias/{categoriaId}/subcategorias/{id} -> SubcategoriaResponse
export async function buscarPorId(categoriaId, id) {
  const { data } = await api.get(`/api/categorias/${categoriaId}/subcategorias/${id}`)
  return data
}

// POST /api/categorias/{categoriaId}/subcategorias -> SubcategoriaResponse (somente ADMIN)
export async function criar(categoriaId, { nome }) {
  const { data } = await api.post(`/api/categorias/${categoriaId}/subcategorias`, { nome })
  return data
}

// PUT /api/categorias/{categoriaId}/subcategorias/{id} -> SubcategoriaResponse (somente ADMIN)
export async function atualizar(categoriaId, id, { nome }) {
  const { data } = await api.put(`/api/categorias/${categoriaId}/subcategorias/${id}`, { nome })
  return data
}

// DELETE /api/categorias/{categoriaId}/subcategorias/{id} (somente ADMIN)
export async function deletar(categoriaId, id) {
  await api.delete(`/api/categorias/${categoriaId}/subcategorias/${id}`)
}