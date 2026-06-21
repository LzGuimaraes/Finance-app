import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import * as categoriaService from '../services/categoriaService'
import { extrairMensagemErro } from '../services/erros'
import ModalFormulario from '../components/ModalFormulario'
import ModalConfirmacao from '../components/ModalConfirmacao'
import '../components/AdminLayout.css'

export default function Categorias() {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()
  const ehAdmin = usuario?.role === 'admin'

  const [categorias, setCategorias] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erroLista, setErroLista] = useState('')

  const [modalAberto, setModalAberto] = useState(null) // null | 'criar' | { tipo: 'editar', categoria }
  const [categoriaParaExcluir, setCategoriaParaExcluir] = useState(null)

  useEffect(() => {
    carregarCategorias()
  }, [])

  async function carregarCategorias() {
    setCarregando(true)
    setErroLista('')
    try {
      const dados = await categoriaService.listar()
      setCategorias(dados)
    } catch (err) {
      setErroLista(extrairMensagemErro(err, 'Não foi possível carregar as categorias.'))
    } finally {
      setCarregando(false)
    }
  }

  function handleSair() {
    sair()
    navigate('/login', { replace: true })
  }

  async function handleCriar(nome) {
    try {
      const nova = await categoriaService.criar({ nome })
      setCategorias((atual) => [...atual, nova].sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR')))
      setModalAberto(null)
    } catch (err) {
      throw Object.assign(new Error(), { mensagemAmigavel: extrairMensagemErro(err, 'Não foi possível criar a categoria.') })
    }
  }

  async function handleEditar(id, nome) {
    try {
      const atualizada = await categoriaService.atualizar(id, { nome })
      setCategorias((atual) =>
        atual
          .map((c) => (c.id === id ? atualizada : c))
          .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'))
      )
      setModalAberto(null)
    } catch (err) {
      throw Object.assign(new Error(), { mensagemAmigavel: extrairMensagemErro(err, 'Não foi possível salvar as alterações.') })
    }
  }

  async function handleExcluir(categoria) {
    try {
      await categoriaService.deletar(categoria.id)
      setCategorias((atual) => atual.filter((c) => c.id !== categoria.id))
      setCategoriaParaExcluir(null)
    } catch (err) {
      throw Object.assign(new Error(), { mensagemAmigavel: extrairMensagemErro(err, 'Não foi possível excluir a categoria.') })
    }
  }

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
            <h1 className="admin-titulo">Categorias de investimento</h1>
            <p className="admin-subtitulo">
              Catálogo global usado para organizar metas de alocação e o histórico
              patrimonial. {ehAdmin ? 'Como administrador, você pode criar, renomear e remover categorias.' : ''}
            </p>
          </div>

          {ehAdmin && (
            <button className="botao-novo" onClick={() => setModalAberto('criar')}>
              <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                <path d="M8 2v12M2 8h12" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/>
              </svg>
              Nova categoria
            </button>
          )}
        </div>

        {erroLista && <div className="alerta alerta-erro" style={{ marginBottom: '1.2rem' }}>{erroLista}</div>}

        {carregando ? (
          <div className="admin-lista">
            <div className="admin-skeleton" />
            <div className="admin-skeleton" />
            <div className="admin-skeleton" />
          </div>
        ) : categorias.length === 0 && !erroLista ? (
          <div className="admin-vazio">
            <p>Nenhuma categoria cadastrada ainda.</p>
            {ehAdmin && (
              <button className="botao-novo" onClick={() => setModalAberto('criar')}>
                Criar primeira categoria
              </button>
            )}
          </div>
        ) : (
          <ul className="admin-lista">
            {categorias.map((categoria) => (
              <li key={categoria.id} className="admin-item">
                <Link to={`/categorias/${categoria.id}/subcategorias`} className="admin-item-link">
                  <div className="admin-item-principal">
                    <span className="admin-item-nome">{categoria.nome}</span>
                    <span className="admin-item-meta">Ver subcategorias</span>
                  </div>
                </Link>

                <div className="admin-item-acoes">
                  {ehAdmin && (
                    <>
                      <button
                        className="botao-icone"
                        title="Renomear categoria"
                        aria-label={`Renomear ${categoria.nome}`}
                        onClick={() => setModalAberto({ tipo: 'editar', categoria })}
                      >
                        <svg width="15" height="15" viewBox="0 0 16 16" fill="none">
                          <path d="M11.5 2.5l2 2L5 13l-2.7.7L3 11 11.5 2.5z" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round"/>
                        </svg>
                      </button>
                      <button
                        className="botao-icone botao-icone-perigo"
                        title="Excluir categoria"
                        aria-label={`Excluir ${categoria.nome}`}
                        onClick={() => setCategoriaParaExcluir(categoria)}
                      >
                        <svg width="15" height="15" viewBox="0 0 16 16" fill="none">
                          <path d="M3 4.5h10M6.5 4.5V3a1 1 0 011-1h1a1 1 0 011 1v1.5M4.5 4.5l.6 8.2a1 1 0 001 .8h3.8a1 1 0 001-.8l.6-8.2" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                      </button>
                    </>
                  )}
                  <span className="admin-item-seta" aria-hidden="true">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <path d="M6 3l5 5-5 5" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                  </span>
                </div>
              </li>
            ))}
          </ul>
        )}
      </main>

      {modalAberto === 'criar' && (
        <ModalFormulario
          titulo="Nova categoria"
          onSalvar={handleCriar}
          onFechar={() => setModalAberto(null)}
        />
      )}

      {modalAberto?.tipo === 'editar' && (
        <ModalFormulario
          titulo="Renomear categoria"
          valorInicial={modalAberto.categoria.nome}
          onSalvar={(nome) => handleEditar(modalAberto.categoria.id, nome)}
          onFechar={() => setModalAberto(null)}
        />
      )}

      {categoriaParaExcluir && (
        <ModalConfirmacao
          titulo="Excluir categoria"
          mensagem={
            <>
              Tem certeza que deseja excluir <strong>{categoriaParaExcluir.nome}</strong>?
              Todas as subcategorias vinculadas também serão excluídas. Esta ação não pode ser desfeita.
            </>
          }
          onConfirmar={() => handleExcluir(categoriaParaExcluir)}
          onFechar={() => setCategoriaParaExcluir(null)}
        />
      )}
    </div>
  )
}