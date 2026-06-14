package Luiz.Finance.Luiz.Ativo;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ativos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtivoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String ticker;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(name = "cotacao_atual")
    private Double cotacaoAtual;

    @Column(name = "data_atualizacao")
    private OffsetDateTime dataAtualizacao;
}