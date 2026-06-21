import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import * as ativoService from '../services/ativoService'
import { extrairMensagemErro } from '../services/erros'
import ModalFormularioAtivo from '../components/ModalFormularioAtivo'
import ModalAtualizarCotacao from '../components/ModalAtualizarCotacao'
import ModalConfirmacao from '../components/ModalConfirmacao'
import '../components/AdminLayout.css'

function formatarCotacao(valor) {
  if (valor == null) return '—'
  return Number(valor).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
    minimumFractionDigits: 2,
    maximumFractionDigits: 6,
  })
}

function formatarData(data) {
  if (!data) return '—'
  return new Date(data).toLocaleString('pt-BR')
}

export default function Ativos() {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()
  const ehAdmin = usuario?.role === 'admin'

  const [ativos, setAtivos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erroLista, setErroLista] = useState('')
  const [busca, setBusca] = useState('')

  const [modalAberto, setModalAberto] = useState(null) // null | 'criar' | { tipo: 'editar', ativo }
  const [ativoParaCotacao, setAtivoParaCotacao] = useState(null)
  const [ativoParaExcluir, setAtivoParaExcluir] = useState(null)

  useEffect(() => {
    carregarAtivos()
  }, [])

  async function carregarAtivos() {
    setCarregando(true)
    setErroLista('')
    try {
      const dados = await ativoService.listar()
      setAtivos(ordenar(dados))
    } catch (err) {
      setErroLista(extrairMensagemErro(err, 'Não foi possível carregar os ativos.'))
    } finally {
      setCarregando(false)
    }
  }

  function ordenar(lista) {
    return [...lista].sort((a, b) => a.ticker.localeCompare(b.ticker, 'pt-BR'))
  }

  function handleSair() {
    sair()
    navigate('/login', { replace: true })
  }

  async function handleCriar(dadosAtivo) {
    try {
      const novo = await ativoService.criar(dadosAtivo)
      setAtivos((atual) => ordenar([...atual, novo]))
      setModalAberto(null)
    } catch (err) {
      throw Object.assign(new Error(), { mensagemAmigavel: extrairMensagemErro(err, 'Não foi possível criar o ativo.') })
    }
  }

  async function handleEditar(id, dadosAtivo) {
    try {
      const atualizado = await ativoService.atualizar(id, dadosAtivo)
      setAtivos((atual) => ordenar(atual.map((a) => (a.id === id ? atualizado : a))))
      setModalAberto(null)
    } catch (err) {
      throw Object.assign(new Error(), { mensagemAmigavel: extrairMensagemErro(err, 'Não foi possível salvar as alterações.') })
    }
  }

  async function handleAtualizarCotacao(id, novaCotacao) {
    try {
      const atualizado = await ativoService.atualizarCotacao(id, novaCotacao)
      setAtivos((atual) => ordenar(atual.map((a) => (a.id === id ? atualizado : a))))
      setAtivoParaCotacao(null)
    } catch (err) {
      throw Object.assign(new Error(), { mensagemAmigavel: extrairMensagemErro(err, 'Não foi possível atualizar a cotação.') })
    }
  }

  async function handleExcluir(ativo) {
    try {
      await ativoService.deletar(ativo.id)
      setAtivos((atual) => atual.filter((a) => a.id !== ativo.id))
      setAtivoParaExcluir(null)
    } catch (err) {
      throw Object.assign(new Error(), { mensagemAmigavel: extrairMensagemErro(err, 'Não foi possível excluir o ativo. Verifique se ele não está em uso em alguma carteira.') })
    }
  }

  const ativosFiltrados = useMemo(() => {
    const termo = busca.trim().toLowerCase()
    if (!termo) return ativos
    return ativos.filter(
      (a) => a.ticker.toLowerCase().includes(termo) || a.nome.toLowerCase().includes(termo)
    )
  }, [ativos, busca])

  return (
    <div className="admin-pagina">
      <header className="admin-cabecalho">
        <Link to="/perfil" className="admin-marca">
          <svg viewBox="0 0 32 32" width="20" height="20" fill="none">
            <path d="M4 24 L11 14 L17 19 L28 6" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round"/>
            <circle cx="28" cy="6" r="2.4" fill="currentColor"/>
          </svg>
          Patrimônio
        </Link>
        <div className="admin-cabecalho-acoes">
          <Link to="/carteira" className="link-secundario">Minha carteira</Link>
          <Link to="/perfil" className="link-secundario">Meu perfil</Link>
          <button className="botao-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="admin-conteudo">
        <div className="admin-topo">
          <div>
            <h1 className="admin-titulo">Ativos</h1>
            <p className="admin-subtitulo">
              Catálogo global de ativos financeiros (ações, FIIs, ETFs, criptomoedas etc.)
              usado nas carteiras dos usuários. {ehAdmin ? 'Como administrador, você pode criar, editar, atualizar cotações e remover ativos.' : ''}
            </p>
          </div>

          {ehAdmin && (
            <button className="botao-novo" onClick={() => setModalAberto('criar')}>
              <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                <path d="M8 2v12M2 8h12" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/>
              </svg>
              Novo ativo
            </button>
          )}
        </div>

        {!carregando && ativos.length > 0 && (
          <div className="campo" style={{ marginBottom: '1.2rem', maxWidth: '20rem' }}>
            <label htmlFor="busca-ativo">Buscar por ticker ou nome</label>
            <input
              id="busca-ativo"
              type="text"
              placeholder="Ex: PETR4"
              value={busca}
              onChange={(e) => setBusca(e.target.value)}
            />
          </div>
        )}

        {erroLista && <div className="alerta alerta-erro" style={{ marginBottom: '1.2rem' }}>{erroLista}</div>}

        {carregando ? (
          <div className="admin-lista">
            <div className="admin-skeleton" />
            <div className="admin-skeleton" />
            <div className="admin-skeleton" />
          </div>
        ) : ativos.length === 0 && !erroLista ? (
          <div className="admin-vazio">
            <p>Nenhum ativo cadastrado ainda.</p>
            {ehAdmin && (
              <button className="botao-novo" onClick={() => setModalAberto('criar')}>
                Criar primeiro ativo
              </button>
            )}
          </div>
        ) : ativosFiltrados.length === 0 ? (
          <div className="admin-vazio">
            <p>Nenhum ativo encontrado para "{busca}".</p>
          </div>
        ) : (
          <ul className="admin-lista">
            {ativosFiltrados.map((ativo) => (
              <li key={ativo.id} className="admin-item">
                <div className="admin-item-principal">
                  <span className="admin-item-nome">
                    {ativo.ticker} — {ativo.nome}
                  </span>
                  <span className="admin-item-meta">
                    {ativo.tipo} · {formatarCotacao(ativo.cotacaoAtual)} · atualizado em {formatarData(ativo.dataAtualizacao)}
                  </span>
                </div>

                {ehAdmin && (
                  <div className="admin-item-acoes">
                    <button
                      className="botao-icone"
                      title="Atualizar cotação"
                      aria-label={`Atualizar cotação de ${ativo.ticker}`}
                      onClick={() => setAtivoParaCotacao(ativo)}
                    >
                      <svg width="15" height="15" viewBox="0 0 16 16" fill="none">
                        <path d="M2 9.5l3-3.5 2.5 2L13 3" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round"/>
                        <path d="M9.5 3H13v3.5" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round"/>
                      </svg>
                    </button>
                    <button
                      className="botao-icone"
                      title="Editar ativo"
                      aria-label={`Editar ${ativo.ticker}`}
                      onClick={() => setModalAberto({ tipo: 'editar', ativo })}
                    >
                      <svg width="15" height="15" viewBox="0 0 16 16" fill="none">
                        <path d="M11.5 2.5l2 2L5 13l-2.7.7L3 11 11.5 2.5z" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round"/>
                      </svg>
                    </button>
                    <button
                      className="botao-icone botao-icone-perigo"
                      title="Excluir ativo"
                      aria-label={`Excluir ${ativo.ticker}`}
                      onClick={() => setAtivoParaExcluir(ativo)}
                    >
                      <svg width="15" height="15" viewBox="0 0 16 16" fill="none">
                        <path d="M3 4.5h10M6.5 4.5V3a1 1 0 011-1h1a1 1 0 011 1v1.5M4.5 4.5l.6 8.2a1 1 0 001 .8h3.8a1 1 0 001-.8l.6-8.2" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round"/>
                      </svg>
                    </button>
                  </div>
                )}
              </li>
            ))}
          </ul>
        )}
      </main>

      {modalAberto === 'criar' && (
        <ModalFormularioAtivo
          titulo="Novo ativo"
          onSalvar={handleCriar}
          onFechar={() => setModalAberto(null)}
        />
      )}

      {modalAberto?.tipo === 'editar' && (
        <ModalFormularioAtivo
          titulo="Editar ativo"
          valorInicial={modalAberto.ativo}
          onSalvar={(dados) => handleEditar(modalAberto.ativo.id, dados)}
          onFechar={() => setModalAberto(null)}
        />
      )}

      {ativoParaCotacao && (
        <ModalAtualizarCotacao
          ativo={ativoParaCotacao}
          onSalvar={(novaCotacao) => handleAtualizarCotacao(ativoParaCotacao.id, novaCotacao)}
          onFechar={() => setAtivoParaCotacao(null)}
        />
      )}

      {ativoParaExcluir && (
        <ModalConfirmacao
          titulo="Excluir ativo"
          mensagem={
            <>
              Tem certeza que deseja excluir <strong>{ativoParaExcluir.ticker}</strong>?
              Esta ação não pode ser desfeita. Se o ativo estiver presente em alguma
              carteira, a exclusão será bloqueada.
            </>
          }
          onConfirmar={() => handleExcluir(ativoParaExcluir)}
          onFechar={() => setAtivoParaExcluir(null)}
        />
      )}
    </div>
  )
}