package Luiz.Finance.Luiz.CarteiraAtivo.exception;

import Luiz.Finance.Luiz.Usuarios.exception.NegocioException;
import org.springframework.http.HttpStatus;

public class CarteiraAtivoJaExisteException extends NegocioException {

    public CarteiraAtivoJaExisteException() {
        super("Você já possui este ativo na sua carteira. Edite a posição existente em vez de criar uma nova.", HttpStatus.CONFLICT);
    }
}