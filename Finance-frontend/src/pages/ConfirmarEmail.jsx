import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import AuthLayout from '../components/AuthLayout'
import * as authService from '../services/authService'
import { extrairMensagemErro } from '../services/erros'
import '../components/forms.css'

export default function ConfirmarEmail() {
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token')

  const [status, setStatus] = useState(token ? 'carregando' : 'erro')
  const [mensagem, setMensagem] = useState(
    token ? '' : 'Link de confirmação inválido: token não encontrado.'
  )

  useEffect(() => {
    if (!token) {
      return
    }

    authService.confirmarEmail(token)
      .then((resposta) => {
        setStatus('sucesso')
        setMensagem(resposta.mensagem)
      })
      .catch((err) => {
        setStatus('erro')
        setMensagem(extrairMensagemErro(err, 'Não foi possível confirmar seu e-mail.'))
      })
  }, [token])

  return (
    <AuthLayout titulo="Confirmação de e-mail">
      {status === 'carregando' && (
        <p style={{ color: 'var(--cor-tinta-suave)' }}>Confirmando seu e-mail…</p>
      )}

      {status === 'sucesso' && (
        <div className="alerta alerta-sucesso">{mensagem}</div>
      )}

      {status === 'erro' && (
        <div className="alerta alerta-erro">{mensagem}</div>
      )}

      <p className="rodape-form">
        <Link to="/login" className="link-secundario">Voltar para o login</Link>
      </p>
    </AuthLayout>
  )
}
