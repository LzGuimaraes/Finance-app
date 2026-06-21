package Luiz.Finance.Luiz.CategoriaInvestimento.exception;

import Luiz.Finance.Luiz.Usuarios.exception.NegocioException;
import org.springframework.http.HttpStatus;

public class CategoriaJaExisteException extends NegocioException {

    public CategoriaJaExisteException(String nome) {
        super("Já existe uma categoria de investimento com o nome '" + nome + "'.", HttpStatus.CONFLICT);
    }
}