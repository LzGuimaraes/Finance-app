package Luiz.Finance.Luiz.Transacao;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import Luiz.Finance.Luiz.CategoriaGasto.CategoriaGastoModel;
import Luiz.Finance.Luiz.Usuarios.UsuarioModel;

@Entity
@Table(name = "transacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransacaoModel {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_gasto_id", nullable = false)
    private CategoriaGastoModel categoriaGasto;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private Double valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransacao status;

    @Column(name = "data_transacao", nullable = false)
    private java.time.LocalDate dataTransacao;
}