package Luiz.Finance.Luiz.Ativo.dto;

import Luiz.Finance.Luiz.Ativo.AtivoModel;
import Luiz.Finance.Luiz.Ativo.dto.AtivoDTOs.AtivoResponse;
import org.springframework.stereotype.Component;

@Component
public class AtivoMapper {

    public AtivoResponse toResponse(AtivoModel ativo) {
        return new AtivoResponse(
                ativo.getId(),
                ativo.getTicker(),
                ativo.getNome(),
                ativo.getTipo(),
                ativo.getCotacaoAtual(),
                ativo.getDataAtualizacao()
        );
    }
}