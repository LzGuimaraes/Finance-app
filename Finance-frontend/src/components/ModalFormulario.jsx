import { useState } from 'react'

export default function ModalFormulario({ titulo, valorInicial = '', onSalvar, onFechar }) {
  const [nome, setNome] = useState(valorInicial)
  const [enviando, setEnviando] = useState(false)
  const [erro, setErro] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setErro('')

    const nomeAjustado = nome.trim()
    if (!nomeAjustado) {
      setErro('Nome é obrigatório')
      return
    }
    if (nomeAjustado.length > 100) {
      setErro('Nome deve ter no máximo 100 caracteres')
      return
    }

    setEnviando(true)
    try {
      await onSalvar(nomeAjustado)
    } catch (err) {
      setErro(err.mensagemAmigavel || 'Não foi possível salvar.')
      setEnviando(false)
    }
  }

  return (
    <div className="admin-modal-fundo" onMouseDown={onFechar}>
      <div className="admin-modal" onMouseDown={(e) => e.stopPropagation()}>
        <h2 className="admin-modal-titulo">{titulo}</h2>

        <form className="form" onSubmit={handleSubmit}>
          {erro && <div className="alerta alerta-erro">{erro}</div>}

          <div className="campo">
            <label htmlFor="nome-item">Nome</label>
            <input
              id="nome-item"
              type="text"
              maxLength={100}
              autoFocus
              placeholder="Ex: Renda Fixa"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              aria-invalid={Boolean(erro)}
              required
            />
          </div>

          <div className="admin-modal-acoes">
            <button
              type="button"
              className="botao-secundario"
              onClick={onFechar}
              disabled={enviando}
            >
              Cancelar
            </button>
            <button type="submit" className="botao-primario" style={{ margin: 0 }} disabled={enviando}>
              {enviando ? 'Salvando…' : 'Salvar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}