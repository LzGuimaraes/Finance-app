package Luiz.Finance.Luiz.CategoriaInvestimento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaInvestimentoDTOs {

    public record CategoriaRequest(
            @NotBlank(message = "Nome é obrigatório")
            @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
            String nome
    ) {}

    public record CategoriaResponse(
            Integer id,
            String nome
    ) {}
}