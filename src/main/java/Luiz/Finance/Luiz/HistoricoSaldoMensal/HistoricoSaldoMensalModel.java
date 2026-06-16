package Luiz.Finance.Luiz.HistoricoSaldoMensal;

import Luiz.Finance.Luiz.SubCategoriaInvestimento.SubcategoriaInvestimentoModel;
import Luiz.Finance.Luiz.Usuarios.UsuarioModel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "historico_saldos_mensais")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoSaldoMensalModel {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategoria_id", nullable = false)
    private SubcategoriaInvestimentoModel subcategoria;

    @Column(name = "data_referencia", nullable = false)
    private java.time.LocalDate dataReferencia;

    @Column(name = "saldo_consolidado", precision = 15, scale = 2, nullable = false)
    private BigDecimal saldoConsolidado;
}