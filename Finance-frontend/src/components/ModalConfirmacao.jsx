import { useState } from 'react'

export default function ModalConfirmacao({ titulo, mensagem, onConfirmar, onFechar }) {
  const [excluindo, setExcluindo] = useState(false)
  const [erro, setErro] = useState('')

  async function handleConfirmar() {
    setErro('')
    setExcluindo(true)
    try {
      await onConfirmar()
    } catch (err) {
      setErro(err.mensagemAmigavel || 'Não foi possível excluir.')
      setExcluindo(false)
    }
  }

  return (
    <div className="admin-modal-fundo" onMouseDown={onFechar}>
      <div className="admin-modal" onMouseDown={(e) => e.stopPropagation()}>
        <h2 className="admin-modal-titulo">{titulo}</h2>

        {erro && <div className="alerta alerta-erro" style={{ marginBottom: '1.1rem' }}>{erro}</div>}

        <p className="admin-modal-texto">{mensagem}</p>

        <div className="admin-modal-acoes">
          <button
            type="button"
            className="botao-secundario"
            onClick={onFechar}
            disabled={excluindo}
          >
            Cancelar
          </button>
          <button
            type="button"
            className="botao-perigo"
            onClick={handleConfirmar}
            disabled={excluindo}
          >
            {excluindo ? 'Excluindo…' : 'Excluir'}
          </button>
        </div>
      </div>
    </div>
  )
}