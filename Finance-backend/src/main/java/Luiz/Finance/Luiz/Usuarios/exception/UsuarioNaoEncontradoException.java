package main.java.Luiz.Finance.Luiz.Usuarios.exception;

import org.springframework.http.HttpStatus;

public class UsuarioNaoEncontradoException extends NegocioException {

    public UsuarioNaoEncontradoException() {
        super("Usuário não encontrado.", HttpStatus.NOT_FOUND);
    }
}