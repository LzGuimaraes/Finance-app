package Luiz.Finance.Luiz.SubCategoriaInvestimento.exception;

import Luiz.Finance.Luiz.Usuarios.exception.NegocioException;
import org.springframework.http.HttpStatus;

public class SubcategoriaNaoEncontradaException extends NegocioException {

    public SubcategoriaNaoEncontradaException() {
        super("Subcategoria de investimento não encontrada.", HttpStatus.NOT_FOUND);
    }
}