package Luiz.Finance.Luiz.Usuarios.dto;

import Luiz.Finance.Luiz.Usuarios.RoleUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AuthDTOs {

    // ── Cadastro ─────────────────────────────────────────────────
    public record CadastroRequest(
            @NotBlank(message = "Nome é obrigatório")
            @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
            String nome,

            @NotBlank(message = "E-mail é obrigatório")
            @Email(message = "E-mail inválido")
            @Size(max = 255)
            String email,

            @NotBlank(message = "Senha é obrigatória")
            @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
            String senha
    ) {}

    // ── Login ────────────────────────────────────────────────────
    public record LoginRequest(
            @NotBlank(message = "E-mail é obrigatório")
            @Email(message = "E-mail inválido")
            String email,

            @NotBlank(message = "Senha é obrigatória")
            String senha
    ) {}

    // ── Resposta de tokens ───────────────────────────────────────
    public record TokenResponse(
            String accessToken,
            String refreshToken,
            String tipo,
            long expiracaoEmSegundos
    ) {}

    // ── Refresh token request ────────────────────────────────────
    public record RefreshTokenRequest(
            @NotBlank(message = "Refresh token é obrigatório")
            String refreshToken
    ) {}

    // ── Perfil do usuário ────────────────────────────────────────
    public record PerfilResponse(
            UUID id,
            String nome,
            String email,
            RoleUsuario role,
            OffsetDateTime dataCriacao,
            boolean emailConfirmado
    ) {}

    // ── Reset de senha ───────────────────────────────────────────
    public record SolicitarResetSenhaRequest(
            @NotBlank(message = "E-mail é obrigatório")
            @Email(message = "E-mail inválido")
            String email
    ) {}

    public record ResetSenhaRequest(
            @NotBlank(message = "Token é obrigatório")
            String token,

            @NotBlank(message = "Nova senha é obrigatória")
            @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
            String novaSenha
    ) {}

    // ── Resposta genérica de mensagem ────────────────────────────
    public record MensagemResponse(String mensagem) {}
}