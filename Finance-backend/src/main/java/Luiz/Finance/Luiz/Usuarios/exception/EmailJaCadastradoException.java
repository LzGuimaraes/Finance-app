package Luiz.Finance.Luiz.Usuarios.exception;

import org.springframework.http.HttpStatus;

public class EmailJaCadastradoException extends NegocioException {

    public EmailJaCadastradoException(String email) {
        super("O e-mail '" + email + "' já está em uso.", HttpStatus.CONFLICT);
    }
}