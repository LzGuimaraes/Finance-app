package Luiz.Finance.Luiz.CategoriaInvestimento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaInvestimentoRepository extends JpaRepository<CategoriaInvestimentoModel, Integer> {

    Optional<CategoriaInvestimentoModel> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);
}