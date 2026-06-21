import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import * as categoriaService from '../services/categoriaService'
import * as subcategoriaService from '../services/subcategoriaService'
import { extrairMensagemErro } from '../services/erros'
import ModalFormulario from '../components/ModalFormulario'
import ModalConfirmacao from '../components/ModalConfirmacao'
import '../components/AdminLayout.css'

export default function Subcategorias() {
  const { categoriaId } = useParams()
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()
  const ehAdmin = usuario?.role === 'admin'

  const [categoria, setCategoria] = useState(null)
  const [subcategorias, setSubcategorias] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erroLista, setErroLista] = useState('')

  const [modalAberto, setModalAberto] = useState(null) // null | 'criar' | { tipo: 'editar', subcategoria }
  const [subcategoriaParaExcluir, setSubcategoriaParaExcluir] = useState(null)

  useEffect(() => {
    carregarDados()
  }, [categoriaId])

  async function carregarDados() {
    setCarregando(true)
    setErroLista('')
    try {
      const [categoriaDados, subcategoriasDados] = await Promise.all([
        categoriaService.buscarPorId(categoriaId),
        subcategoriaService.listar(categoriaId),
      ])
      setCategoria(categoriaDados)
      setSubcategorias(subcategoriasDados)
    } catch (err) {
      setErroLista(extrairMensagemErro(err, 'Não foi possível carregar as subcategorias.'))
    } finally {
      setCarregando(false)
    }
  }

  function handleSair() {
    sair()
    navigate('/login', { replace: true })
  }

  function ordenar(lista) {
    return [...lista].sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'))
  }

  async function handleCriar(nome) {
    try {
      const nova = await subcategoriaService.criar(categoriaId, { nome })
      setSubcategorias((atual) => ordenar([...atual, nova]))
      setModalAberto(null)
    } catch (err) {
      throw Object.assign(new Error(), { mensagemAmigavel: extrairMensagemErro(err, 'Não foi possível criar a subcategoria.') })
    }
  }

  async function handleEditar(id, nome) {
    try {
      const atualizada = await subcategoriaService.atualizar(categoriaId, id, { nome })
      setSubcategorias((atual) => ordenar(atual.map((s) => (s.id === id ? atualizada : s))))
      setModalAberto(null)
    } catch (err) {
      throw Object.assign(new Error(), { mensagemAmigavel: extrairMensagemErro(err, 'Não foi possível salvar as alterações.') })
    }
  }

  async function handleExcluir(subcategoria) {
    try {
      await subcategoriaService.deletar(categoriaId, subcategoria.id)
      setSubcategorias((atual) => atual.filter((s) => s.id !== subcategoria.id))
      setSubcategoriaParaExcluir(null)
    } catch (err) {
      throw Object.assign(new Error(), { mensagemAmigavel: extrairMensagemErro(err, 'Não foi possível excluir a subcategoria.') })
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
          <Link to="/perfil" className="link-secundario">Meu perfil</Link>
          <button className="botao-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="admin-conteudo">
        <p className="admin-trilha">
          <Link to="/categorias">Categorias</Link> / {carregando ? '…' : categoria?.nome || 'Subcategorias'}
        </p>

        <div className="admin-topo">
          <div>
            <h1 className="admin-titulo">
              {carregando ? 'Subcategorias' : `Subcategorias de ${categoria?.nome ?? ''}`}
            </h1>
            <p className="admin-subtitulo">
              Usadas para detalhar metas de alocação e o histórico de saldos dentro
              desta categoria. {ehAdmin ? 'Como administrador, você pode criar, renomear e remover itens.' : ''}
            </p>
          </div>

          {ehAdmin && !carregando && !erroLista && (
            <button className="botao-novo" onClick={() => setModalAberto('criar')}>
              <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                <path d="M8 2v12M2 8h12" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/>
              </svg>
              Nova subcategoria
            </button>
          )}
        </div>

        {erroLista && <div className="alerta alerta-erro" style={{ marginBottom: '1.2rem' }}>{erroLista}</div>}

        {carregando ? (
          <div className="admin-lista">
            <div className="admin-skeleton" />
            <div className="admin-skeleton" />
          </div>
        ) : subcategorias.length === 0 && !erroLista ? (
          <div className="admin-vazio">
            <p>Nenhuma subcategoria cadastrada nesta categoria ainda.</p>
            {ehAdmin && (
              <button className="botao-novo" onClick={() => setModalAberto('criar')}>
                Criar primeira subcategoria
              </button>
            )}
          </div>
        ) : (
          <ul className="admin-lista">
            {subcategorias.map((subcategoria) => (
              <li key={subcategoria.id} className="admin-item">
                <div className="admin-item-principal">
                  <span className="admin-item-nome">{subcategoria.nome}</span>
                </div>

                {ehAdmin && (
                  <div className="admin-item-acoes">
                    <button
                      className="botao-icone"
                      title="Renomear subcategoria"
                      aria-label={`Renomear ${subcategoria.nome}`}
                      onClick={() => setModalAberto({ tipo: 'editar', subcategoria })}
                    >
                      <svg width="15" height="15" viewBox="0 0 16 16" fill="none">
                        <path d="M11.5 2.5l2 2L5 13l-2.7.7L3 11 11.5 2.5z" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round"/>
                      </svg>
                    </button>
                    <button
                      className="botao-icone botao-icone-perigo"
                      title="Excluir subcategoria"
                      aria-label={`Excluir ${subcategoria.nome}`}
                      onClick={() => setSubcategoriaParaExcluir(subcategoria)}
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
        <ModalFormulario
          titulo="Nova subcategoria"
          onSalvar={handleCriar}
          onFechar={() => setModalAberto(null)}
        />
      )}

      {modalAberto?.tipo === 'editar' && (
        <ModalFormulario
          titulo="Renomear subcategoria"
          valorInicial={modalAberto.subcategoria.nome}
          onSalvar={(nome) => handleEditar(modalAberto.subcategoria.id, nome)}
          onFechar={() => setModalAberto(null)}
        />
      )}

      {subcategoriaParaExcluir && (
        <ModalConfirmacao
          titulo="Excluir subcategoria"
          mensagem={
            <>
              Tem certeza que deseja excluir <strong>{subcategoriaParaExcluir.nome}</strong>?
              Esta ação não pode ser desfeita.
            </>
          }
          onConfirmar={() => handleExcluir(subcategoriaParaExcluir)}
          onFechar={() => setSubcategoriaParaExcluir(null)}
        />
      )}
    </div>
  )
}