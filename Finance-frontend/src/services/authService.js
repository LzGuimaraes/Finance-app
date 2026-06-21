import api from './api'

// POST /api/auth/cadastro -> MensagemResponse
export async function cadastrar({ nome, email, senha }) {
  const { data } = await api.post('/api/auth/cadastro', { nome, email, senha })
  return data
}

// POST /api/auth/login -> TokenResponse
export async function login({ email, senha }) {
  const { data } = await api.post('/api/auth/login', { email, senha })
  return data
}

// POST /api/auth/refresh -> TokenResponse
export async function refreshToken(refreshTokenValue) {
  const { data } = await api.post('/api/auth/refresh', { refreshToken: refreshTokenValue })
  return data
}

// GET /api/auth/confirmar-email?token=... -> MensagemResponse
export async function confirmarEmail(token) {
  const { data } = await api.get('/api/auth/confirmar-email', { params: { token } })
  return data
}

// GET /api/auth/perfil -> PerfilResponse
export async function buscarPerfil() {
  const { data } = await api.get('/api/auth/perfil')
  return data
}

// POST /api/auth/solicitar-reset-senha -> MensagemResponse
export async function solicitarResetSenha(email) {
  const { data } = await api.post('/api/auth/solicitar-reset-senha', { email })
  return data
}

// POST /api/auth/reset-senha -> MensagemResponse
export async function resetarSenha({ token, novaSenha }) {
  const { data } = await api.post('/api/auth/reset-senha', { token, novaSenha })
  return data
}
