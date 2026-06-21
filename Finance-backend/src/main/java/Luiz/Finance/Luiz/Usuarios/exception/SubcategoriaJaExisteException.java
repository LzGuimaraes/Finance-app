package Luiz.Finance.Luiz.SubCategoriaInvestimento.exception;

import Luiz.Finance.Luiz.Usuarios.exception.NegocioException;
import org.springframework.http.HttpStatus;

public class SubcategoriaJaExisteException extends NegocioException {

    public SubcategoriaJaExisteException(String nome) {
        super("Já existe uma subcategoria com o nome '" + nome + "' nesta categoria.", HttpStatus.CONFLICT);
    }
}