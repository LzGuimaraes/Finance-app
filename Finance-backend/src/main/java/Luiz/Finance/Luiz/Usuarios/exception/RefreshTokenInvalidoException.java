package main.java.Luiz.Finance.Luiz.Usuarios.exception;

import org.springframework.http.HttpStatus;

public class RefreshTokenInvalidoException extends NegocioException {

    public RefreshTokenInvalidoException() {
        super("Refresh token inválido. Faça login novamente.", HttpStatus.UNAUTHORIZED);
    }
}