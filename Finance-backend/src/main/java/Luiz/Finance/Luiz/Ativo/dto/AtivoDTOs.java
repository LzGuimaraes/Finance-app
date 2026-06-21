package Luiz.Finance.Luiz.Ativo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class AtivoDTOs {

    public record AtivoRequest(
            @NotBlank(message = "Ticker é obrigatório")
            @Size(max = 20, message = "Ticker deve ter no máximo 20 caracteres")
            String ticker,

            @NotBlank(message = "Nome é obrigatório")
            @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
            String nome,

            @NotBlank(message = "Tipo é obrigatório")
            @Size(max = 50, message = "Tipo deve ter no máximo 50 caracteres")
            String tipo,

            @PositiveOrZero(message = "Cotação não pode ser negativa")
            BigDecimal cotacaoAtual
    ) {}

    public record AtivoCotacaoRequest(
            @PositiveOrZero(message = "Cotação não pode ser negativa")
            BigDecimal cotacaoAtual
    ) {}

    public record AtivoResponse(
            Integer id,
            String ticker,
            String nome,
            String tipo,
            BigDecimal cotacaoAtual,
            OffsetDateTime dataAtualizacao
    ) {}
}