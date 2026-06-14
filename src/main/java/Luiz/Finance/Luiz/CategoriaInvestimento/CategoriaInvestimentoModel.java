package Luiz.Finance.Luiz.CategoriaInvestimento;

import jakarta.persistence.*;
import lombok.*;

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
}