package Luiz.Finance.Luiz.SubCategoriaInvestimento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SubcategoriaInvestimentoDTOs {

    public record SubcategoriaRequest(
            @NotBlank(message = "Nome é obrigatório")
            @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
            String nome
    ) {}

    public record SubcategoriaResponse(
            Integer id,
            Integer categoriaId,
            String nome
    ) {}
}