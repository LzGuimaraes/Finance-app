package Luiz.Finance.Luiz.Usuarios.controller;

import Luiz.Finance.Luiz.Usuarios.UsuarioModel;
import Luiz.Finance.Luiz.Usuarios.dto.AuthDTOs.*;
import Luiz.Finance.Luiz.Usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    // ── POST /api/auth/cadastro ───────────────────────────────────
    @PostMapping("/cadastro")
    public ResponseEntity<MensagemResponse> cadastrar(@Valid @RequestBody CadastroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.cadastrar(request));
    }

    // ── POST /api/auth/login ──────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }

    // ── POST /api/auth/refresh ────────────────────────────────────
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(usuarioService.refreshToken(request));
    }

    // ── GET /api/auth/confirmar-email?token=... ───────────────────
    @GetMapping("/confirmar-email")
    public ResponseEntity<MensagemResponse> confirmarEmail(@RequestParam String token) {
        return ResponseEntity.ok(usuarioService.confirmarEmail(token));
    }

    // ── GET /api/auth/perfil ──────────────────────────────────────
    @GetMapping("/perfil")
    public ResponseEntity<PerfilResponse> perfil(@AuthenticationPrincipal UsuarioModel usuario) {
        return ResponseEntity.ok(usuarioService.buscarPerfil(usuario.getEmail()));
    }

    // ── POST /api/auth/solicitar-reset-senha ──────────────────────
    @PostMapping("/solicitar-reset-senha")
    public ResponseEntity<MensagemResponse> solicitarReset(
            @Valid @RequestBody SolicitarResetSenhaRequest request) {
        return ResponseEntity.ok(usuarioService.solicitarResetSenha(request));
    }

    // ── POST /api/auth/reset-senha ────────────────────────────────
    @PostMapping("/reset-senha")
    public ResponseEntity<MensagemResponse> resetarSenha(
            @Valid @RequestBody ResetSenhaRequest request) {
        return ResponseEntity.ok(usuarioService.resetarSenha(request));
    }
}