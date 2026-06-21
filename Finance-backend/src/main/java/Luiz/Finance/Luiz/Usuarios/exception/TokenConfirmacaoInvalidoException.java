package main.java.Luiz.Finance.Luiz.Usuarios.exception;

import org.springframework.http.HttpStatus;

public class TokenConfirmacaoInvalidoException extends NegocioException {

    public TokenConfirmacaoInvalidoException() {
        super("Token de confirmação de e-mail inválido ou já utilizado.", HttpStatus.BAD_REQUEST);
    }
}