package Luiz.Finance.Luiz.SubCategoriaInvestimento.dto;

import Luiz.Finance.Luiz.SubCategoriaInvestimento.SubcategoriaInvestimentoModel;
import Luiz.Finance.Luiz.SubCategoriaInvestimento.dto.SubcategoriaInvestimentoDTOs.SubcategoriaResponse;
import org.springframework.stereotype.Component;

@Component
public class SubcategoriaInvestimentoMapper {

    public SubcategoriaResponse toResponse(SubcategoriaInvestimentoModel subcategoria) {
        return new SubcategoriaResponse(
                subcategoria.getId(),
                subcategoria.getCategoria().getId(),
                subcategoria.getNome()
        );
    }
}