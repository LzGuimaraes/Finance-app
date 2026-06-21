package Luiz.Finance.Luiz.Usuarios;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel usuario;

    @Column(name = "expiracao", nullable = false)
    private OffsetDateTime expiracao;

    @Column(name = "revogado")
    @Builder.Default
    private boolean revogado = false;

    public boolean isExpirado() {
        return OffsetDateTime.now().isAfter(expiracao);
    }

    public boolean isValido() {
        return !revogado && !isExpirado();
    }
}