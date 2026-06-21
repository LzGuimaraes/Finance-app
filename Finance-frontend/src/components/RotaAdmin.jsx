import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'

export default function RotaAdmin({ children }) {
  const { usuario, carregando } = useAuth()

  if (carregando) {
    return <div className="tela-carregando">Carregando…</div>
  }

  if (!usuario) {
    return <Navigate to="/login" replace />
  }

  if (usuario.role !== 'admin') {
    return <Navigate to="/perfil" replace />
  }

  return children
}