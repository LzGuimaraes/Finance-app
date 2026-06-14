package Luiz.Finance.Luiz.CarteiraAtivo;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import Luiz.Finance.Luiz.Ativo.AtivoModel;
import Luiz.Finance.Luiz.Usuarios.UsuarioModel;

@Entity
@Table(name = "carteira_ativos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarteiraAtivoModel {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ativo_id", nullable = false)
    private AtivoModel ativo;

    @Column(nullable = false)
    private Double quantidade;

    @Column(name = "preco_medio", nullable = false)
    private Double precoMedio;

    @Column(name = "nota_qualidade")
    private Integer notaQualidade;
}