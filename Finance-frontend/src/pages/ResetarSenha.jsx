import { useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import AuthLayout from '../components/AuthLayout'
import * as authService from '../services/authService'
import { extrairMensagemErro } from '../services/erros'
import '../components/forms.css'

export default function ResetarSenha() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const token = searchParams.get('token')

  const [novaSenha, setNovaSenha] = useState('')
  const [enviando, setEnviando] = useState(false)
  const [erro, setErro] = useState('')
  const [erroSenha, setErroSenha] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setErro('')
    setErroSenha('')

    if (novaSenha.length < 8) {
      setErroSenha('Senha deve ter no mínimo 8 caracteres')
      return
    }

    setEnviando(true)
    try {
      await authService.resetarSenha({ token, novaSenha })
      navigate('/login', {
        replace: true,
        state: { mensagem: 'Senha redefinida com sucesso! Faça login com sua nova senha.' },
      })
    } catch (err) {
      setErro(extrairMensagemErro(err, 'Não foi possível redefinir sua senha.'))
    } finally {
      setEnviando(false)
    }
  }

  if (!token) {
    return (
      <AuthLayout titulo="Redefinir senha">
        <div className="alerta alerta-erro">
          Link inválido: token de redefinição não encontrado. Solicite um novo link.
        </div>
        <p className="rodape-form">
          <Link to="/solicitar-reset-senha" className="link-secundario">Solicitar novo link</Link>
        </p>
      </AuthLayout>
    )
  }

  return (
    <AuthLayout
      titulo="Redefinir senha"
      subtitulo="Escolha uma nova senha para sua conta."
    >
      {erro && <div className="alerta alerta-erro" style={{ marginBottom: '1.1rem' }}>{erro}</div>}

      <form className="form" onSubmit={handleSubmit}>
        <div className="campo">
          <label htmlFor="novaSenha">Nova senha</label>
          <input
            id="novaSenha"
            type="password"
            autoComplete="new-password"
            placeholder="Mínimo de 8 caracteres"
            value={novaSenha}
            onChange={(e) => setNovaSenha(e.target.value)}
            aria-invalid={Boolean(erroSenha)}
            required
          />
          {erroSenha && <span className="campo-erro">{erroSenha}</span>}
        </div>

        <button type="submit" className="botao-primario" disabled={enviando}>
          {enviando ? 'Salvando…' : 'Redefinir senha'}
        </button>
      </form>
    </AuthLayout>
  )
}
