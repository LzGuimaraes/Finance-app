package main.java.Luiz.Finance.Luiz.Usuarios.exception;

import org.springframework.http.HttpStatus;

/**
 * Classe base para exceções de regra de negócio do módulo de usuários.
 * Cada subclasse define o HttpStatus apropriado, permitindo que o
 * GlobalExceptionHandler trate todas de forma genérica.
 */
public abstract class NegocioException extends RuntimeException {

    private final HttpStatus status;

    protected NegocioException(String mensagem, HttpStatus status) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}