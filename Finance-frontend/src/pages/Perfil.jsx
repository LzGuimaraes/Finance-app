import { useAuth } from '../context/useAuth'
import { useNavigate } from 'react-router-dom'
import './Perfil.css'

export default function Perfil() {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()

  function handleSair() {
    sair()
    navigate('/login', { replace: true })
  }

  const iniciais = usuario.nome
    .split(' ')
    .slice(0, 2)
    .map((parte) => parte[0]?.toUpperCase())
    .join('')

  return (
    <div className="perfil-pagina">
      <header className="perfil-cabecalho">
        <div className="perfil-marca">
          <svg viewBox="0 0 32 32" width="20" height="20" fill="none">
            <path d="M4 24 L11 14 L17 19 L28 6" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round"/>
            <circle cx="28" cy="6" r="2.4" fill="currentColor"/>
          </svg>
          Patrimônio
        </div>
        <button className="botao-sair" onClick={handleSair}>Sair</button>
      </header>

      <main className="perfil-conteudo">
        <div className="perfil-card">
          <div className="perfil-avatar">{iniciais}</div>
          <h1 className="perfil-nome">{usuario.nome}</h1>
          <p className="perfil-email">{usuario.email}</p>

          <dl className="perfil-detalhes">
            <div className="perfil-detalhe-item">
              <dt>Perfil de acesso</dt>
              <dd>{usuario.role === 'admin' ? 'Administrador' : 'Usuário comum'}</dd>
            </div>
            <div className="perfil-detalhe-item">
              <dt>E-mail confirmado</dt>
              <dd>{usuario.emailConfirmado ? 'Sim' : 'Não'}</dd>
            </div>
            <div className="perfil-detalhe-item">
              <dt>Conta criada em</dt>
              <dd>{new Date(usuario.dataCriacao).toLocaleDateString('pt-BR')}</dd>
            </div>
          </dl>
        </div>
      </main>
    </div>
  )
}
