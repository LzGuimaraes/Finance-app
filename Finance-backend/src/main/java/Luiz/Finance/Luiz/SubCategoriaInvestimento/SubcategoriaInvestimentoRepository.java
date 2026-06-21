package Luiz.Finance.Luiz.SubCategoriaInvestimento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubcategoriaInvestimentoRepository extends JpaRepository<SubcategoriaInvestimentoModel, Integer> {

    List<SubcategoriaInvestimentoModel> findAllByCategoriaId(Integer categoriaId);

    // Garante que a subcategoria pertence exatamente à categoria informada na URL
    // (essencial para a rota nested: /categorias/{categoriaId}/subcategorias/{id}).
    Optional<SubcategoriaInvestimentoModel> findByIdAndCategoriaId(Integer id, Integer categoriaId);

    boolean existsByCategoriaIdAndNomeIgnoreCase(Integer categoriaId, String nome);
}