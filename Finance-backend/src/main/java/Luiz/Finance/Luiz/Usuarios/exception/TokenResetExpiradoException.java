package Luiz.Finance.Luiz.Usuarios.exception;

import org.springframework.http.HttpStatus;

public class TokenResetExpiradoException extends NegocioException {

    public TokenResetExpiradoException() {
        super("Token de redefinição de senha expirado. Solicite um novo.", HttpStatus.GONE);
    }
}