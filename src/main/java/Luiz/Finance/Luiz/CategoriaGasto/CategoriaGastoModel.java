package Luiz.Finance.Luiz.CategoriaGasto;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias_gastos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaGastoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nome;
}