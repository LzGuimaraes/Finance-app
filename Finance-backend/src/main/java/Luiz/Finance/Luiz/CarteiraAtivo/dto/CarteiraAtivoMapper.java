package Luiz.Finance.Luiz.CarteiraAtivo.dto;

import Luiz.Finance.Luiz.CarteiraAtivo.CarteiraAtivoModel;
import Luiz.Finance.Luiz.CarteiraAtivo.dto.CarteiraAtivoDTOs.CarteiraAtivoResponse;
import org.springframework.stereotype.Component;

@Component
public class CarteiraAtivoMapper {

    public CarteiraAtivoResponse toResponse(CarteiraAtivoModel carteiraAtivo) {
        return new CarteiraAtivoResponse(
                carteiraAtivo.getId(),
                carteiraAtivo.getAtivo().getId(),
                carteiraAtivo.getAtivo().getTicker(),
                carteiraAtivo.getAtivo().getNome(),
                carteiraAtivo.getQuantidade(),
                carteiraAtivo.getPrecoMedio(),
                carteiraAtivo.getNotaQualidade()
        );
    }
}