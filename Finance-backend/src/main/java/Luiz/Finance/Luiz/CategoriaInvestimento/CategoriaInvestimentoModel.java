package Luiz.Finance.Luiz.CategoriaInvestimento;

import Luiz.Finance.Luiz.SubCategoriaInvestimento.SubcategoriaInvestimentoModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias_investimento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaInvestimentoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nome;

    // Cascade ALL + orphanRemoval: deletar a categoria deleta suas subcategorias
    // (espelha o ON DELETE CASCADE da FK fk_categoria no schema SQL).
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubcategoriaInvestimentoModel> subcategorias = new ArrayList<>();
}