package Luiz.Finance.Luiz.Usuarios.exception;

import org.springframework.http.HttpStatus;

public class TokenResetInvalidoException extends NegocioException {

    public TokenResetInvalidoException() {
        super("Token de redefinição de senha inválido.", HttpStatus.BAD_REQUEST);
    }
}