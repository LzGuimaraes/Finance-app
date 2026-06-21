import { useState } from 'react'
import { Link } from 'react-router-dom'
import AuthLayout from '../components/AuthLayout'
import * as authService from '../services/authService'
import { extrairMensagemErro } from '../services/erros'
import '../components/forms.css'

export default function SolicitarResetSenha() {
  const [email, setEmail] = useState('')
  const [enviando, setEnviando] = useState(false)
  const [erro, setErro] = useState('')
  const [enviado, setEnviado] = useState(false)
  const [mensagem, setMensagem] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      const resposta = await authService.solicitarResetSenha(email)
      setMensagem(resposta.mensagem)
      setEnviado(true)
    } catch (err) {
      setErro(extrairMensagemErro(err, 'Não foi possível processar sua solicitação.'))
    } finally {
      setEnviando(false)
    }
  }

  return (
    <AuthLayout
      titulo="Recuperar senha"
      subtitulo="Informe seu e-mail e enviaremos as instruções de redefinição."
    >
      {erro && <div className="alerta alerta-erro" style={{ marginBottom: '1.1rem' }}>{erro}</div>}

      {enviado ? (
        <div className="alerta alerta-sucesso">{mensagem}</div>
      ) : (
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

          <button type="submit" className="botao-primario" disabled={enviando}>
            {enviando ? 'Enviando…' : 'Enviar instruções'}
          </button>
        </form>
      )}

      <p className="rodape-form">
        <Link to="/login" className="link-secundario">Voltar para o login</Link>
      </p>
    </AuthLayout>
  )
}
