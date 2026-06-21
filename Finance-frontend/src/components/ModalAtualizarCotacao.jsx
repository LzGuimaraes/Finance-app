import { useState } from 'react'

export default function ModalAtualizarCotacao({ ativo, onSalvar, onFechar }) {
  const [cotacaoAtual, setCotacaoAtual] = useState(
    ativo.cotacaoAtual != null ? String(ativo.cotacaoAtual) : ''
  )
  const [enviando, setEnviando] = useState(false)
  const [erro, setErro] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setErro('')

    const valor = Number(cotacaoAtual.replace(',', '.'))
    if (cotacaoAtual.trim() === '' || Number.isNaN(valor) || valor < 0) {
      setErro('Informe um valor numérico maior ou igual a zero')
      return
    }

    setEnviando(true)
    try {
      await onSalvar(valor)
    } catch (err) {
      setErro(err.mensagemAmigavel || 'Não foi possível atualizar a cotação.')
      setEnviando(false)
    }
  }

  return (
    <div className="admin-modal-fundo" onMouseDown={onFechar}>
      <div className="admin-modal" onMouseDown={(e) => e.stopPropagation()}>
        <h2 className="admin-modal-titulo">Atualizar cotação — {ativo.ticker}</h2>

        <form className="form" onSubmit={handleSubmit}>
          {erro && <div className="alerta alerta-erro">{erro}</div>}

          <div className="campo">
            <label htmlFor="nova-cotacao">Nova cotação</label>
            <input
              id="nova-cotacao"
              type="text"
              inputMode="decimal"
              autoFocus
              placeholder="Ex: 32.50"
              value={cotacaoAtual}
              onChange={(e) => setCotacaoAtual(e.target.value)}
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
              {enviando ? 'Salvando…' : 'Atualizar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}