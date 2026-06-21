// O GlobalExceptionHandler do backend retorna RFC 7807 ProblemDetail.
// Para erros de validação (@Valid), o corpo inclui um campo extra "erros"
// no formato { campo: mensagem }. Para os demais, usamos "detail".
export function extrairMensagemErro(error, mensagemPadrao = 'Algo deu errado. Tente novamente.') {
  const dados = error?.response?.data

  if (!dados) {
    return mensagemPadrao
  }

  if (dados.erros && typeof dados.erros === 'object') {
    const mensagens = Object.values(dados.erros)
    if (mensagens.length > 0) {
      return mensagens.join(' ')
    }
  }

  if (dados.detail) {
    return dados.detail
  }

  if (dados.title) {
    return dados.title
  }

  return mensagemPadrao
}
