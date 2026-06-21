import { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import AuthLayout from '../components/AuthLayout'
import { useAuth } from '../context/useAuth'
import { extrairMensagemErro } from '../services/erros'
import '../components/forms.css'

export default function Login() {
  const navigate = useNavigate()
  const location = useLocation()
  const { entrar } = useAuth()

  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [enviando, setEnviando] = useState(false)
  const [erro, setErro] = useState('')

  const mensagemRota = location.state?.mensagem

  async function handleSubmit(e) {
    e.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      await entrar({ email, senha })
      navigate('/perfil', { replace: true })
    } catch (err) {
      setErro(extrairMensagemErro(err, 'E-mail ou senha incorretos.'))
    } finally {
      setEnviando(false)
    }
  }

  return (
    <AuthLayout
      titulo="Entrar"
      subtitulo="Acesse sua conta para continuar acompanhando seu patrimônio."
    >
      {mensagemRota && <div className="alerta alerta-sucesso" style={{ marginBottom: '1.1rem' }}>{mensagemRota}</div>}
      {erro && <div className="alerta alerta-erro" style={{ marginBottom: '1.1rem' }}>{erro}</div>}

      <form className="form" onSubmit={handleSubmit}>
        <div className="campo">
          <label htmlFor="email">E-mail</label>
          <input
            id="email"
            type="email"
            autoComplete="email"
            placeholder="voce@exemplo.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>

        <div className="campo">
          <label htmlFor="senha">Senha</label>
          <input
            id="senha"
            type="password"
            autoComplete="current-password"
            placeholder="••••••••"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            required
          />
        </div>

        <div style={{ textAlign: 'right' }}>
          <Link to="/solicitar-reset-senha" className="link-secundario">
            Esqueci minha senha
          </Link>
        </div>

        <button type="submit" className="botao-primario" disabled={enviando}>
          {enviando ? 'Entrando…' : 'Entrar'}
        </button>
      </form>

      <p className="rodape-form">
        Ainda não tem conta?{' '}
        <Link to="/cadastro" className="link-secundario">Criar conta</Link>
      </p>
    </AuthLayout>
  )
}
