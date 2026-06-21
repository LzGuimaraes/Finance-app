package Luiz.Finance.Luiz.Usuarios.service;

import Luiz.Finance.Luiz.Usuarios.RoleUsuario;
import Luiz.Finance.Luiz.Usuarios.RefreshToken;
import Luiz.Finance.Luiz.Usuarios.UsuarioModel;
import Luiz.Finance.Luiz.Usuarios.UsuarioRepository;
import Luiz.Finance.Luiz.Usuarios.dto.AuthDTOs.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;

    // Injeção via setter com @Lazy para quebrar dependência circular:
    // UsuarioService → AuthenticationManager → SecurityConfig → UsuarioService
    private AuthenticationManager authenticationManager;

    @Autowired
    public void setAuthenticationManager(@Lazy AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // ── Cadastro ─────────────────────────────────────────────────
    @Transactional
    public MensagemResponse cadastrar(CadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail já está em uso.");
        }

        String tokenConfirmacao = UUID.randomUUID().toString();

        UsuarioModel usuario = UsuarioModel.builder()
                .nome(request.nome())
                .email(request.email().toLowerCase())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .role(RoleUsuario.usuario_comum)
                .dataCriacao(OffsetDateTime.now())
                .emailConfirmado(false)
                .tokenConfirmacaoEmail(tokenConfirmacao)
                .build();

        usuarioRepository.save(usuario);
        emailService.enviarConfirmacaoEmail(usuario.getEmail(), tokenConfirmacao);

        return new MensagemResponse("Cadastro realizado! Verifique seu e-mail para ativar a conta.");
    }

    // ── Login ────────────────────────────────────────────────────
    public TokenResponse login(LoginRequest request) {
        UsuarioModel usuario = usuarioRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));

        if (!usuario.isEmailConfirmado()) {
            throw new DisabledException("E-mail ainda não confirmado. Verifique sua caixa de entrada.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.senha())
        );

        String accessToken = jwtService.gerarToken(usuario);
        RefreshToken refreshToken = refreshTokenService.criar(usuario);

        return new TokenResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtService.getExpiracaoMs() / 1000
        );
    }

    // ── Refresh Token ────────────────────────────────────────────
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.buscarPorToken(request.refreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Refresh token não encontrado."));

        if (!refreshToken.isValido()) {
            throw new IllegalArgumentException("Refresh token expirado ou revogado.");
        }

        UsuarioModel usuario = refreshToken.getUsuario();
        String novoAccessToken = jwtService.gerarToken(usuario);
        RefreshToken novoRefreshToken = refreshTokenService.criar(usuario);

        return new TokenResponse(
                novoAccessToken,
                novoRefreshToken.getToken(),
                "Bearer",
                jwtService.getExpiracaoMs() / 1000
        );
    }

    // ── Confirmação de e-mail ────────────────────────────────────
    @Transactional
    public MensagemResponse confirmarEmail(String token) {
        UsuarioModel usuario = usuarioRepository.findByTokenConfirmacaoEmail(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de confirmação inválido."));

        usuario.setEmailConfirmado(true);
        usuario.setTokenConfirmacaoEmail(null);
        usuarioRepository.save(usuario);

        return new MensagemResponse("E-mail confirmado com sucesso! Você já pode fazer login.");
    }

    // ── Perfil ───────────────────────────────────────────────────
    public PerfilResponse buscarPerfil(String email) {
        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        return new PerfilResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getDataCriacao(),
                usuario.isEmailConfirmado()
        );
    }

    // ── Reset de senha ───────────────────────────────────────────
    @Transactional
    public MensagemResponse solicitarResetSenha(SolicitarResetSenhaRequest request) {
        usuarioRepository.findByEmail(request.email().toLowerCase()).ifPresent(usuario -> {
            String token = UUID.randomUUID().toString();
            usuario.setTokenResetSenha(token);
            usuario.setExpiracaoTokenReset(OffsetDateTime.now().plusHours(1));
            usuarioRepository.save(usuario);
            emailService.enviarResetSenha(usuario.getEmail(), token);
        });
        return new MensagemResponse("Se o e-mail existir, você receberá as instruções em breve.");
    }

    @Transactional
    public MensagemResponse resetarSenha(ResetSenhaRequest request) {
        UsuarioModel usuario = usuarioRepository.findByTokenResetSenha(request.token())
                .orElseThrow(() -> new IllegalArgumentException("Token de reset inválido."));

        if (usuario.getExpiracaoTokenReset() == null ||
                OffsetDateTime.now().isAfter(usuario.getExpiracaoTokenReset())) {
            throw new IllegalArgumentException("Token de reset expirado. Solicite um novo.");
        }

        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        usuario.setTokenResetSenha(null);
        usuario.setExpiracaoTokenReset(null);
        usuarioRepository.save(usuario);

        refreshTokenService.revogarTodosDoUsuario(usuario.getId());

        return new MensagemResponse("Senha redefinida com sucesso!");
    }

    // ── UserDetailsService ───────────────────────────────────────
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }
}