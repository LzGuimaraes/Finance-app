package Luiz.Finance.Luiz.CarteiraAtivo.exception;

import Luiz.Finance.Luiz.Usuarios.exception.NegocioException;
import org.springframework.http.HttpStatus;

public class CarteiraAtivoNaoEncontradoException extends NegocioException {

    public CarteiraAtivoNaoEncontradoException() {
        super("Posição não encontrada na carteira.", HttpStatus.NOT_FOUND);
    }
}