import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import * as carteiraAtivoService from '../services/carteiraAtivoService'
import * as ativoService from '../services/ativoService'
import { extrairMensagemErro } from '../services/erros'
import ModalFormularioCarteiraAtivo from '../components/ModalFormularioCarteiraAtivo'
import ModalConfirmacao from '../components/ModalConfirmacao'
import '../components/AdminLayout.css'

function formatarMoeda(valor) {
  if (valor == null) return '—'
  return Number(valor).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
    minimumFractionDigits: 2,
    maximumFractionDigits: 6,
  })
}

function formatarQuantidade(valor) {
  if (valor == null) return '—'
  return Number(valor).toLocaleString('pt-BR', { maximumFractionDigits: 6 })
}

export default function CarteiraAtivos() {
  const { sair } = useAuth()
  const navigate = useNavigate()

  const [posicoes, setPosicoes] = useState([])
  const [ativosDisponiveis, setAtivosDisponiveis] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erroLista, setErroLista] = useState('')
  const [busca, setBusca] = useState('')

  const [modalAberto, setModalAberto] = useState(null) // null | 'criar' | { tipo: 'editar', posicao }
  const [posicaoParaExcluir, setPosicaoParaExcluir] = useState(null)

  useEffect(() => {
    carregarDados()
  }, [])

  async function carregarDados() {
    setCarregando(true)
    setErroLista('')
    try {
      const [posicoesDados, ativosDados] = await Promise.all([
        carteiraAtivoService.listar(),
        ativoService.listar(),
      ])
      setPosicoes(ordenar(posicoesDados))
      setAtivosDisponiveis(ativosDados)
    } catch (err) {
      setErroLista(extrairMensagemErro(err, 'Não foi possível carregar sua carteira.'))
    } finally {
      setCarregando(false)
    }
  }

  function ordenar(lista) {
    return [...lista].sort((a, b) => a.ativoTicker.localeCompare(b.ativoTicker, 'pt-BR'))
  }

  function handleSair() {
    sair()
    navigate('/login', { replace: true })
  }

  // Ativos que o usuário ainda não possui na carteira — evita tentar criar
  // duplicado (a API rejeita com 409, mas filtrar já direciona melhor).
  const ativosParaSelecao = useMemo(() => {
    const idsNaCarteira = new Set(posicoes.map((p) => p.ativoId))
    return ativosDisponiveis
      .filter((a) => !idsNaCarteira.has(a.id))
      .sort((a, b) => a.ticker.localeCompare(b.ticker, 'pt-BR'))
  }, [ativosDisponiveis, posicoes])

  async function handleCriar(dadosPosicao) {
    try {
      const nova = await carteiraAtivoService.criar(dadosPosicao)
      setPosicoes((atual) => ordenar([...atual, nova]))
      setModalAberto(null)
    } catch (err) {
      throw Object.assign(new Error(), { mensagemAmigavel: extrairMensagemErro(err, 'Não foi possível adicionar o ativo à carteira.') })
    }
  }

  async function handleEditar(id, dadosPosicao) {
    try {
      const atualizada = await carteiraAtivoService.atualizar(id, dadosPosicao)
      setPosicoes((atual) => ordenar(atual.map((p) => (p.id === id ? atualizada : p))))
      setModalAberto(null)
    } catch (err) {
      throw Object.assign(new Error(), { mensagemAmigavel: extrairMensagemErro(err, 'Não foi possível salvar as alterações.') })
    }
  }

  async function handleExcluir(posicao) {
    try {
      await carteiraAtivoService.deletar(posicao.id)
      setPosicoes((atual) => atual.filter((p) => p.id !== posicao.id))
      setPosicaoParaExcluir(null)
    } catch (err) {
      throw Object.assign(new Error(), { mensagemAmigavel: extrairMensagemErro(err, 'Não foi possível remover o ativo da carteira.') })
    }
  }

  const posicoesFiltradas = useMemo(() => {
    const termo = busca.trim().toLowerCase()
    if (!termo) return posicoes
    return posicoes.filter(
      (p) => p.ativoTicker.toLowerCase().includes(termo) || p.ativoNome.toLowerCase().includes(termo)
    )
  }, [posicoes, busca])

  const valorTotalCarteira = useMemo(() => {
    return posicoes.reduce((total, p) => total + Number(p.quantidade) * Number(p.precoMedio), 0)
  }, [posicoes])

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
          <Link to="/perfil" className="link-secundario">Meu perfil</Link>
          <button className="botao-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="admin-conteudo">
        <div className="admin-topo">
          <div>
            <h1 className="admin-titulo">Minha carteira</h1>
            <p className="admin-subtitulo">
              Seus ativos, com quantidade, preço médio e nota de qualidade.
              {!carregando && posicoes.length > 0 && (
                <> Valor investido (preço médio): <strong>{formatarMoeda(valorTotalCarteira)}</strong>.</>
              )}
            </p>
          </div>

          {!carregando && ativosDisponiveis.length > 0 && (
            <button
              className="botao-novo"
              onClick={() => setModalAberto('criar')}
              disabled={ativosParaSelecao.length === 0}
              title={ativosParaSelecao.length === 0 ? 'Você já possui todos os ativos do catálogo' : undefined}
            >
              <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                <path d="M8 2v12M2 8h12" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/>
              </svg>
              Adicionar ativo
            </button>
          )}
        </div>

        {!carregando && posicoes.length > 0 && (
          <div className="campo" style={{ marginBottom: '1.2rem', maxWidth: '20rem' }}>
            <label htmlFor="busca-carteira">Buscar por ticker ou nome</label>
            <input
              id="busca-carteira"
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
        ) : posicoes.length === 0 && !erroLista ? (
          <div className="admin-vazio">
            <p>Você ainda não tem ativos na sua carteira.</p>
            {ativosDisponiveis.length > 0 ? (
              <button className="botao-novo" onClick={() => setModalAberto('criar')}>
                Adicionar primeiro ativo
              </button>
            ) : (
              <p style={{ fontSize: '0.85rem' }}>
                Nenhum ativo está cadastrado no catálogo ainda. Peça a um administrador
                para cadastrar ativos em <Link to="/ativos">Ativos</Link>.
              </p>
            )}
          </div>
        ) : posicoesFiltradas.length === 0 ? (
          <div className="admin-vazio">
            <p>Nenhum ativo encontrado para "{busca}".</p>
          </div>
        ) : (
          <ul className="admin-lista">
            {posicoesFiltradas.map((posicao) => (
              <li key={posicao.id} className="admin-item">
                <div className="admin-item-principal">
                  <span className="admin-item-nome">
                    {posicao.ativoTicker} — {posicao.ativoNome}
                  </span>
                  <span className="admin-item-meta">
                    {formatarQuantidade(posicao.quantidade)} un. · preço médio {formatarMoeda(posicao.precoMedio)}
                    {posicao.notaQualidade != null && <> · nota {posicao.notaQualidade}/10</>}
                  </span>
                </div>

                <div className="admin-item-acoes">
                  <button
                    className="botao-icone"
                    title="Editar posição"
                    aria-label={`Editar posição em ${posicao.ativoTicker}`}
                    onClick={() => setModalAberto({ tipo: 'editar', posicao })}
                  >
                    <svg width="15" height="15" viewBox="0 0 16 16" fill="none">
                      <path d="M11.5 2.5l2 2L5 13l-2.7.7L3 11 11.5 2.5z" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round"/>
                    </svg>
                  </button>
                  <button
                    className="botao-icone botao-icone-perigo"
                    title="Remover da carteira"
                    aria-label={`Remover ${posicao.ativoTicker} da carteira`}
                    onClick={() => setPosicaoParaExcluir(posicao)}
                  >
                    <svg width="15" height="15" viewBox="0 0 16 16" fill="none">
                      <path d="M3 4.5h10M6.5 4.5V3a1 1 0 011-1h1a1 1 0 011 1v1.5M4.5 4.5l.6 8.2a1 1 0 001 .8h3.8a1 1 0 001-.8l.6-8.2" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </main>

      {modalAberto === 'criar' && (
        <ModalFormularioCarteiraAtivo
          titulo="Adicionar ativo à carteira"
          ativosDisponiveis={ativosParaSelecao}
          onSalvar={handleCriar}
          onFechar={() => setModalAberto(null)}
        />
      )}

      {modalAberto?.tipo === 'editar' && (
        <ModalFormularioCarteiraAtivo
          titulo="Editar posição"
          ativosDisponiveis={ativosParaSelecao}
          valorInicial={modalAberto.posicao}
          onSalvar={(dados) => handleEditar(modalAberto.posicao.id, dados)}
          onFechar={() => setModalAberto(null)}
        />
      )}

      {posicaoParaExcluir && (
        <ModalConfirmacao
          titulo="Remover ativo da carteira"
          mensagem={
            <>
              Tem certeza que deseja remover <strong>{posicaoParaExcluir.ativoTicker}</strong> da
              sua carteira? Esta ação não pode ser desfeita.
            </>
          }
          onConfirmar={() => handleExcluir(posicaoParaExcluir)}
          onFechar={() => setPosicaoParaExcluir(null)}
        />
      )}
    </div>
  )
}