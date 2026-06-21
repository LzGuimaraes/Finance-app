import './AuthLayout.css'

const TICKER_ITENS = [
  { rotulo: 'RESERVA', valor: '+12,4%' },
  { rotulo: 'RENDA FIXA', valor: '+3,1%' },
  { rotulo: 'AÇÕES BR', valor: '+8,7%' },
  { rotulo: 'FIIs', valor: '+5,2%' },
  { rotulo: 'INTL', valor: '+9,9%' },
]

export default function AuthLayout({ titulo, subtitulo, children }) {
  return (
    <div className="auth-layout">
      <aside className="auth-painel">
        <div className="auth-painel-conteudo">
          <div className="auth-marca">
            <span className="auth-marca-icone" aria-hidden="true">
              <svg viewBox="0 0 32 32" width="22" height="22" fill="none">
                <path d="M4 24 L11 14 L17 19 L28 6" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round"/>
                <circle cx="28" cy="6" r="2.4" fill="currentColor"/>
              </svg>
            </span>
            Patrimônio
          </div>

          <h1 className="auth-painel-titulo">
            Cada real, no seu devido lugar.
          </h1>
          <p className="auth-painel-texto">
            Organize ativos, metas de alocação e fluxo de caixa em um só
            lugar — e acompanhe a evolução do seu patrimônio com clareza.
          </p>

          <ul className="auth-ticker" aria-hidden="true">
            {TICKER_ITENS.map((item) => (
              <li key={item.rotulo} className="auth-ticker-item">
                <span className="auth-ticker-rotulo">{item.rotulo}</span>
                <span className="auth-ticker-valor">{item.valor}</span>
              </li>
            ))}
          </ul>
        </div>
      </aside>

      <main className="auth-form-area">
        <div className="auth-form-card">
          <div className="auth-marca auth-marca-mobile">
            <span className="auth-marca-icone" aria-hidden="true">
              <svg viewBox="0 0 32 32" width="20" height="20" fill="none">
                <path d="M4 24 L11 14 L17 19 L28 6" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round"/>
                <circle cx="28" cy="6" r="2.4" fill="currentColor"/>
              </svg>
            </span>
            Patrimônio
          </div>

          <h2 className="auth-form-titulo">{titulo}</h2>
          {subtitulo && <p className="auth-form-subtitulo">{subtitulo}</p>}

          {children}
        </div>
      </main>
    </div>
  )
}
