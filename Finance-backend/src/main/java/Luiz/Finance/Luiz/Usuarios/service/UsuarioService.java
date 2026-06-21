package Luiz.Finance.Luiz.Usuarios.service;

import Luiz.Finance.Luiz.Usuarios.RoleUsuario;
import Luiz.Finance.Luiz.Usuarios.RefreshToken;
import Luiz.Finance.Luiz.Usuarios.UsuarioModel;
import Luiz.Finance.Luiz.Usuarios.UsuarioRepository;
import Luiz.Finance.Luiz.Usuarios.dto.AuthDTOs.*;
import Luiz.Finance.Luiz.Usuarios.exception.EmailJaCadastradoException;
import Luiz.Finance.Luiz.Usuarios.exception.RefreshTokenExpiradoException;
import Luiz.Finance.Luiz.Usuarios.exception.RefreshTokenInvalidoException;
import Luiz.Finance.Luiz.Usuarios.exception.TokenConfirmacaoInvalidoException;
import Luiz.Finance.Luiz.Usuarios.exception.TokenResetExpiradoException;
import Luiz.Finance.Luiz.Usuarios.exception.TokenResetInvalidoException;
import Luiz.Finance.Luiz.Usuarios.exception.UsuarioNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
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
        String email = request.email().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException(email);
        }

        String tokenConfirmacao = UUID.randomUUID().toString();

        UsuarioModel usuario = UsuarioModel.builder()
                .nome(request.nome())
                .email(email)
                .senhaHash(passwordEncoder.encode(request.senha()))
                .role(RoleUsuario.usuario_comum)
                .dataCriacao(OffsetDateTime.now())
                .emailConfirmado(false)
                .tokenConfirmacaoEmail(tokenConfirmacao)
                .build();

        try {
            usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException ex) {
            // Cobre a corrida entre o existsByEmail() acima e o save():
            // duas requisições simultâneas com o mesmo e-mail podem passar
            // ambas pela verificação e colidir só na constraint UNIQUE do banco.
            throw new EmailJaCadastradoException(email);
        }

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

        // BadCredentialsException é lançada pelo AuthenticationManager
        // e já tratada no GlobalExceptionHandler.
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
                .orElseThrow(RefreshTokenInvalidoException::new);

        if (!refreshToken.isValido()) {
            throw new RefreshTokenExpiradoException();
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
                .orElseThrow(TokenConfirmacaoInvalidoException::new);

        usuario.setEmailConfirmado(true);
        usuario.setTokenConfirmacaoEmail(null);
        usuarioRepository.save(usuario);

        return new MensagemResponse("E-mail confirmado com sucesso! Você já pode fazer login.");
    }

    // ── Perfil ───────────────────────────────────────────────────
    public PerfilResponse buscarPerfil(String email) {
        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(UsuarioNaoEncontradoException::new);

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
        // Mensagem deliberadamente genérica mesmo se o e-mail não existir,
        // para não revelar quais e-mails estão cadastrados na base.
        return new MensagemResponse("Se o e-mail existir, você receberá as instruções em breve.");
    }

    @Transactional
    public MensagemResponse resetarSenha(ResetSenhaRequest request) {
        UsuarioModel usuario = usuarioRepository.findByTokenResetSenha(request.token())
                .orElseThrow(TokenResetInvalidoException::new);

        if (usuario.getExpiracaoTokenReset() == null ||
                OffsetDateTime.now().isAfter(usuario.getExpiracaoTokenReset())) {
            throw new TokenResetExpiradoException();
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