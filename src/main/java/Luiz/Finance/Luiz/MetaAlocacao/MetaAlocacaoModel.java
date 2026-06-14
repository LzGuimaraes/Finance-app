package Luiz.Finance.Luiz.MetaAlocacao;


import Luiz.Finance.Luiz.SubCategoriaInvestimento.SubcategoriaInvestimentoModel;
import Luiz.Finance.Luiz.Usuarios.UsuarioModel;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "metas_alocacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetaAlocacaoModel {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategoria_id", nullable = false)
    private SubcategoriaInvestimentoModel subcategoria;

    @Column(name = "percentual_meta", nullable = false)
    private Double percentualMeta;
}