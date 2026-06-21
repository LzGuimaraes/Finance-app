package Luiz.Finance.Luiz.CarteiraAtivo.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public class CarteiraAtivoDTOs {

    public record CarteiraAtivoRequest(
            @NotNull(message = "Ativo é obrigatório")
            Integer ativoId,

            @NotNull(message = "Quantidade é obrigatória")
            @PositiveOrZero(message = "Quantidade não pode ser negativa")
            BigDecimal quantidade,

            @NotNull(message = "Preço médio é obrigatório")
            @PositiveOrZero(message = "Preço médio não pode ser negativo")
            BigDecimal precoMedio,

            @Min(value = 0, message = "Nota de qualidade deve ser entre 0 e 10")
            @Max(value = 10, message = "Nota de qualidade deve ser entre 0 e 10")
            Integer notaQualidade
    ) {}

    public record CarteiraAtivoResponse(
            UUID id,
            Integer ativoId,
            String ativoTicker,
            String ativoNome,
            BigDecimal quantidade,
            BigDecimal precoMedio,
            Integer notaQualidade
    ) {}
}