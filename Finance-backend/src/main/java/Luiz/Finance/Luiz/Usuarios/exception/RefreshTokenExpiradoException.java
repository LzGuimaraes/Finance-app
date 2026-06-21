package main.java.Luiz.Finance.Luiz.Usuarios.exception;

import org.springframework.http.HttpStatus;

public class RefreshTokenExpiradoException extends NegocioException {

    public RefreshTokenExpiradoException() {
        super("Refresh token expirado ou revogado. Faça login novamente.", HttpStatus.GONE);
    }
}