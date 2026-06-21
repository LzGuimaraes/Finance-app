import { useState } from 'react'

export default function ModalFormularioAtivo({ titulo, valorInicial = null, onSalvar, onFechar }) {
  const [ticker, setTicker] = useState(valorInicial?.ticker ?? '')
  const [nome, setNome] = useState(valorInicial?.nome ?? '')
  const [tipo, setTipo] = useState(valorInicial?.tipo ?? '')
  const [cotacaoAtual, setCotacaoAtual] = useState(
    valorInicial?.cotacaoAtual != null ? String(valorInicial.cotacaoAtual) : ''
  )
  const [enviando, setEnviando] = useState(false)
  const [erro, setErro] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setErro('')

    const tickerAjustado = ticker.trim().toUpperCase()
    const nomeAjustado = nome.trim()
    const tipoAjustado = tipo.trim()

    if (!tickerAjustado) {
      setErro('Ticker é obrigatório')
      return
    }
    if (tickerAjustado.length > 20) {
      setErro('Ticker deve ter no máximo 20 caracteres')
      return
    }
    if (!nomeAjustado) {
      setErro('Nome é obrigatório')
      return
    }
    if (nomeAjustado.length > 255) {
      setErro('Nome deve ter no máximo 255 caracteres')
      return
    }
    if (!tipoAjustado) {
      setErro('Tipo é obrigatório')
      return
    }
    if (tipoAjustado.length > 50) {
      setErro('Tipo deve ter no máximo 50 caracteres')
      return
    }

    let cotacaoNumerica = null
    if (cotacaoAtual.trim() !== '') {
      cotacaoNumerica = Number(cotacaoAtual.replace(',', '.'))
      if (Number.isNaN(cotacaoNumerica) || cotacaoNumerica < 0) {
        setErro('Cotação deve ser um número maior ou igual a zero')
        return
      }
    }

    setEnviando(true)
    try {
      await onSalvar({
        ticker: tickerAjustado,
        nome: nomeAjustado,
        tipo: tipoAjustado,
        cotacaoAtual: cotacaoNumerica,
      })
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
            <label htmlFor="ticker-ativo">Ticker</label>
            <input
              id="ticker-ativo"
              type="text"
              maxLength={20}
              autoFocus
              placeholder="Ex: PETR4"
              value={ticker}
              onChange={(e) => setTicker(e.target.value)}
              aria-invalid={Boolean(erro)}
              required
            />
          </div>

          <div className="campo">
            <label htmlFor="nome-ativo">Nome</label>
            <input
              id="nome-ativo"
              type="text"
              maxLength={255}
              placeholder="Ex: Petrobras PN"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              required
            />
          </div>

          <div className="campo">
            <label htmlFor="tipo-ativo">Tipo</label>
            <input
              id="tipo-ativo"
              type="text"
              maxLength={50}
              placeholder="Ex: Ação, FII, ETF, Cripto"
              value={tipo}
              onChange={(e) => setTipo(e.target.value)}
              required
            />
          </div>

          <div className="campo">
            <label htmlFor="cotacao-ativo">Cotação atual</label>
            <input
              id="cotacao-ativo"
              type="text"
              inputMode="decimal"
              placeholder="Ex: 32.50 (opcional)"
              value={cotacaoAtual}
              onChange={(e) => setCotacaoAtual(e.target.value)}
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