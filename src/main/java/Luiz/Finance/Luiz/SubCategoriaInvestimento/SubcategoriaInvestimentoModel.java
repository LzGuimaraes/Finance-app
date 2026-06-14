package Luiz.Finance.Luiz.SubCategoriaInvestimento;

import Luiz.Finance.Luiz.CategoriaInvestimento.CategoriaInvestimentoModel;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "subcategorias_investimento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubcategoriaInvestimentoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaInvestimentoModel categoria;

    @Column(nullable = false)
    private String nome;
}