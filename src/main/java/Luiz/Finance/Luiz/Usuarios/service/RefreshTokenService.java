package Luiz.Finance.Luiz.Usuarios.service;

import Luiz.Finance.Luiz.Usuarios.RefreshToken;
import Luiz.Finance.Luiz.Usuarios.UsuarioModel;
import Luiz.Finance.Luiz.Usuarios.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiracao-ms:604800000}") // 7 dias padrão
    private long refreshExpiracaoMs;

    @Transactional
    public RefreshToken criar(UsuarioModel usuario) {
        // Revogar tokens anteriores do usuário (política: um refresh por vez)
        refreshTokenRepository.revogarTodosDoUsuario(usuario.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .usuario(usuario)
                .expiracao(OffsetDateTime.now().plusNanos(refreshExpiracaoMs * 1_000_000))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> buscarPorToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void revogar(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevogado(true);
            refreshTokenRepository.save(rt);
        });
    }

    @Transactional
    public void revogarTodosDoUsuario(UUID usuarioId) {
        refreshTokenRepository.revogarTodosDoUsuario(usuarioId);
    }
}