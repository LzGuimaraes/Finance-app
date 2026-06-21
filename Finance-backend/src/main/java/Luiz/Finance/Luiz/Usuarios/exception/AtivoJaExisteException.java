package Luiz.Finance.Luiz.Ativo.exception;

import Luiz.Finance.Luiz.Usuarios.exception.NegocioException;
import org.springframework.http.HttpStatus;

public class AtivoJaExisteException extends NegocioException {

    public AtivoJaExisteException(String ticker) {
        super("Já existe um ativo com o ticker '" + ticker + "'.", HttpStatus.CONFLICT);
    }
}