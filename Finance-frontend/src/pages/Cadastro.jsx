import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import AuthLayout from '../components/AuthLayout'
import * as authService from '../services/authService'
import { extrairMensagemErro } from '../services/erros'
import '../components/forms.css'

export default function Cadastro() {
  const navigate = useNavigate()

  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [enviando, setEnviando] = useState(false)
  const [erro, setErro] = useState('')
  const [erroSenha, setErroSenha] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setErro('')
    setErroSenha('')

    if (senha.length < 8) {
      setErroSenha('Senha deve ter no mínimo 8 caracteres')
      return
    }

    setEnviando(true)
    try {
      await authService.cadastrar({ nome, email, senha })
      navigate('/login', {
        replace: true,
        state: { mensagem: 'Cadastro realizado! Verifique seu e-mail para ativar a conta.' },
      })
    } catch (err) {
      setErro(extrairMensagemErro(err, 'Não foi possível concluir o cadastro.'))
    } finally {
      setEnviando(false)
    }
  }

  return (
    <AuthLayout
      titulo="Criar conta"
      subtitulo="Comece a organizar seus ativos e metas em poucos minutos."
    >
      {erro && <div className="alerta alerta-erro" style={{ marginBottom: '1.1rem' }}>{erro}</div>}

      <form className="form" onSubmit={handleSubmit}>
        <div className="campo">
          <label htmlFor="nome">Nome</label>
          <input
            id="nome"
            type="text"
            autoComplete="name"
            placeholder="Seu nome completo"
            maxLength={100}
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            required
          />
        </div>

        <div className="campo">
          <label htmlFor="email">E-mail</label>
          <input
            id="email"
            type="email"
            autoComplete="email"
            placeholder="voce@exemplo.com"
            maxLength={255}
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
            autoComplete="new-password"
            placeholder="Mínimo de 8 caracteres"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            aria-invalid={Boolean(erroSenha)}
            required
          />
          {erroSenha && <span className="campo-erro">{erroSenha}</span>}
        </div>

        <button type="submit" className="botao-primario" disabled={enviando}>
          {enviando ? 'Criando conta…' : 'Criar conta'}
        </button>
      </form>

      <p className="rodape-form">
        Já tem conta?{' '}
        <Link to="/login" className="link-secundario">Entrar</Link>
      </p>
    </AuthLayout>
  )
}
