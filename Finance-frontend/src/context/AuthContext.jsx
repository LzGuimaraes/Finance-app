import { useEffect, useState } from 'react'
import * as authService from '../services/authService'
import { AuthContext } from './AuthContextObject'

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null)
  const [carregando, setCarregando] = useState(true)

  useEffect(() => {
    async function carregarPerfil() {
      const token = localStorage.getItem('accessToken')
      if (!token) {
        setCarregando(false)
        return
      }
      try {
        const perfil = await authService.buscarPerfil()
        setUsuario(perfil)
      } catch {
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
      } finally {
        setCarregando(false)
      }
    }
    carregarPerfil()
  }, [])

  async function entrar({ email, senha }) {
    const tokenResponse = await authService.login({ email, senha })
    localStorage.setItem('accessToken', tokenResponse.accessToken)
    localStorage.setItem('refreshToken', tokenResponse.refreshToken)
    const perfil = await authService.buscarPerfil()
    setUsuario(perfil)
    return perfil
  }

  function sair() {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    setUsuario(null)
  }

  return (
    <AuthContext.Provider value={{ usuario, carregando, entrar, sair }}>
      {children}
    </AuthContext.Provider>
  )
}
