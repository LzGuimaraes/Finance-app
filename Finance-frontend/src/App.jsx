import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import RotaProtegida from './components/RotaProtegida'

import Login from './pages/Login'
import Cadastro from './pages/Cadastro'
import ConfirmarEmail from './pages/ConfirmarEmail'
import SolicitarResetSenha from './pages/SolicitarResetSenha'
import ResetarSenha from './pages/ResetarSenha'
import Perfil from './pages/Perfil'
import Categorias from './pages/Categorias'
import Subcategorias from './pages/Subcategorias'
import Ativos from './pages/Ativos'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="/login" element={<Login />} />
          <Route path="/cadastro" element={<Cadastro />} />
          <Route path="/confirmar-email" element={<ConfirmarEmail />} />
          <Route path="/solicitar-reset-senha" element={<SolicitarResetSenha />} />
          <Route path="/reset-senha" element={<ResetarSenha />} />
          <Route
            path="/perfil"
            element={
              <RotaProtegida>
                <Perfil />
              </RotaProtegida>
            }
          />
          <Route
            path="/categorias"
            element={
              <RotaProtegida>
                <Categorias />
              </RotaProtegida>
            }
          />
          <Route
            path="/categorias/:categoriaId/subcategorias"
            element={
              <RotaProtegida>
                <Subcategorias />
              </RotaProtegida>
            }
          />
          <Route
            path="/ativos"
            element={
              <RotaProtegida>
                <Ativos />
              </RotaProtegida>
            }
          />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}