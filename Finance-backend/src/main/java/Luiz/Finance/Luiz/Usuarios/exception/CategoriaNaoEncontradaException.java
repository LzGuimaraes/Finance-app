package Luiz.Finance.Luiz.CategoriaInvestimento.exception;

import Luiz.Finance.Luiz.Usuarios.exception.NegocioException;
import org.springframework.http.HttpStatus;

public class CategoriaNaoEncontradaException extends NegocioException {

    public CategoriaNaoEncontradaException() {
        super("Categoria de investimento não encontrada.", HttpStatus.NOT_FOUND);
    }
}