import { useState } from 'react'

export default function ModalFormularioCarteiraAtivo({
  titulo,
  ativosDisponiveis,
  valorInicial = null,
  onSalvar,
  onFechar,
}) {
  const ehEdicao = valorInicial != null

  const [ativoId, setAtivoId] = useState(
    valorInicial?.ativoId != null ? String(valorInicial.ativoId) : ''
  )
  const [quantidade, setQuantidade] = useState(
    valorInicial?.quantidade != null ? String(valorInicial.quantidade) : ''
  )
  const [precoMedio, setPrecoMedio] = useState(
    valorInicial?.precoMedio != null ? String(valorInicial.precoMedio) : ''
  )
  const [notaQualidade, setNotaQualidade] = useState(
    valorInicial?.notaQualidade != null ? String(valorInicial.notaQualidade) : ''
  )
  const [enviando, setEnviando] = useState(false)
  const [erro, setErro] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setErro('')

    if (!ativoId) {
      setErro('Selecione um ativo')
      return
    }

    const quantidadeNumerica = Number(quantidade.replace(',', '.'))
    if (quantidade.trim() === '' || Number.isNaN(quantidadeNumerica) || quantidadeNumerica < 0) {
      setErro('Quantidade deve ser um número maior ou igual a zero')
      return
    }

    const precoMedioNumerico = Number(precoMedio.replace(',', '.'))
    if (precoMedio.trim() === '' || Number.isNaN(precoMedioNumerico) || precoMedioNumerico < 0) {
      setErro('Preço médio deve ser um número maior ou igual a zero')
      return
    }

    let notaNumerica = null
    if (notaQualidade.trim() !== '') {
      notaNumerica = Number(notaQualidade)
      if (!Number.isInteger(notaNumerica) || notaNumerica < 0 || notaNumerica > 10) {
        setErro('Nota de qualidade deve ser um número inteiro entre 0 e 10')
        return
      }
    }

    setEnviando(true)
    try {
      await onSalvar({
        ativoId: Number(ativoId),
        quantidade: quantidadeNumerica,
        precoMedio: precoMedioNumerico,
        notaQualidade: notaNumerica,
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
            <label htmlFor="ativo-carteira">Ativo</label>
            {ehEdicao ? (
              <input
                id="ativo-carteira"
                type="text"
                value={`${valorInicial.ativoTicker} — ${valorInicial.ativoNome}`}
                disabled
              />
            ) : (
              <select
                id="ativo-carteira"
                autoFocus
                value={ativoId}
                onChange={(e) => setAtivoId(e.target.value)}
                aria-invalid={Boolean(erro)}
                required
              >
                <option value="" disabled>
                  Selecione um ativo…
                </option>
                {ativosDisponiveis.map((ativo) => (
                  <option key={ativo.id} value={ativo.id}>
                    {ativo.ticker} — {ativo.nome}
                  </option>
                ))}
              </select>
            )}
            {ehEdicao && (
              <span className="campo-erro" style={{ color: 'var(--cor-tinta-suave)' }}>
                Não é possível alterar o ativo de uma posição existente. Remova e crie uma nova.
              </span>
            )}
          </div>

          <div className="campo">
            <label htmlFor="quantidade-carteira">Quantidade</label>
            <input
              id="quantidade-carteira"
              type="text"
              inputMode="decimal"
              placeholder="Ex: 100"
              value={quantidade}
              onChange={(e) => setQuantidade(e.target.value)}
              required
            />
          </div>

          <div className="campo">
            <label htmlFor="preco-medio-carteira">Preço médio</label>
            <input
              id="preco-medio-carteira"
              type="text"
              inputMode="decimal"
              placeholder="Ex: 32.50"
              value={precoMedio}
              onChange={(e) => setPrecoMedio(e.target.value)}
              required
            />
          </div>

          <div className="campo">
            <label htmlFor="nota-qualidade-carteira">Nota de qualidade (0 a 10)</label>
            <input
              id="nota-qualidade-carteira"
              type="text"
              inputMode="numeric"
              placeholder="Opcional"
              value={notaQualidade}
              onChange={(e) => setNotaQualidade(e.target.value)}
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