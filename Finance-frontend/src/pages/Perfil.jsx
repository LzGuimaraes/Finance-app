import { useAuth } from '../context/useAuth'
import { Link, useNavigate } from 'react-router-dom'
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
        <nav className="perfil-nav">
          <Link to="/categorias" className="perfil-nav-link">Categorias</Link>
          <Link to="/ativos" className="perfil-nav-link">Ativos</Link>
          <button className="botao-sair" onClick={handleSair}>Sair</button>
        </nav>
      </header>

      <main className="perfil-conteudo">
        <div className="perfil-coluna">
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

          <Link to="/categorias" className="perfil-atalho">
            <span className="perfil-atalho-icone" aria-hidden="true">
              <svg width="18" height="18" viewBox="0 0 16 16" fill="none">
                <rect x="2" y="2" width="5" height="5" rx="1" stroke="currentColor" strokeWidth="1.3"/>
                <rect x="9" y="2" width="5" height="5" rx="1" stroke="currentColor" strokeWidth="1.3"/>
                <rect x="2" y="9" width="5" height="5" rx="1" stroke="currentColor" strokeWidth="1.3"/>
                <rect x="9" y="9" width="5" height="5" rx="1" stroke="currentColor" strokeWidth="1.3"/>
              </svg>
            </span>
            <span className="perfil-atalho-texto">
              <span className="perfil-atalho-titulo">Categorias de investimento</span>
              <span className="perfil-atalho-descricao">Gerencie o catálogo de categorias e subcategorias</span>
            </span>
            <span className="perfil-atalho-seta" aria-hidden="true">
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                <path d="M6 3l5 5-5 5" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </span>
          </Link>

          <Link to="/ativos" className="perfil-atalho">
            <span className="perfil-atalho-icone" aria-hidden="true">
              <svg width="18" height="18" viewBox="0 0 16 16" fill="none">
                <path d="M2 13l3-4 2.5 2L13 4" stroke="currentColor" strokeWidth="1.3" strokeLinecap="round" strokeLinejoin="round"/>
                <path d="M9.5 4H13v3.5" stroke="currentColor" strokeWidth="1.3" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </span>
            <span className="perfil-atalho-texto">
              <span className="perfil-atalho-titulo">Ativos</span>
              <span className="perfil-atalho-descricao">Gerencie o catálogo de ativos e suas cotações</span>
            </span>
            <span className="perfil-atalho-seta" aria-hidden="true">
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                <path d="M6 3l5 5-5 5" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </span>
          </Link>
        </div>
      </main>
    </div>
  )
}