package Luiz.Finance.Luiz.CategoriaInvestimento.dto;

import Luiz.Finance.Luiz.CategoriaInvestimento.CategoriaInvestimentoModel;
import Luiz.Finance.Luiz.CategoriaInvestimento.dto.CategoriaInvestimentoDTOs.CategoriaResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoriaInvestimentoMapper {

    public CategoriaResponse toResponse(CategoriaInvestimentoModel categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNome());
    }
}