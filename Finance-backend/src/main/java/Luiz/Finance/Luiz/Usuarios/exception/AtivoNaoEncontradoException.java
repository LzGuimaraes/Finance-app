package Luiz.Finance.Luiz.Ativo.exception;

import Luiz.Finance.Luiz.Usuarios.exception.NegocioException;
import org.springframework.http.HttpStatus;

public class AtivoNaoEncontradoException extends NegocioException {

    public AtivoNaoEncontradoException() {
        super("Ativo não encontrado.", HttpStatus.NOT_FOUND);
    }
}