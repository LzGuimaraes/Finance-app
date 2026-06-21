package Luiz.Finance.Luiz.Usuarios.service;

import Luiz.Finance.Luiz.Usuarios.UsuarioModel;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiracao-ms:3600000}") // 1 hora padrão
    private long expiracaoMs;

    // ── Geração ──────────────────────────────────────────────────
    public String gerarToken(UsuarioModel usuario) {
        return gerarToken(Map.of(), usuario);
    }

    public String gerarToken(Map<String, Object> claims, UsuarioModel usuario) {
        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getEmail())
                .claim("id", usuario.getId().toString())
                .claim("role", usuario.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracaoMs))
                .signWith(getSigningKey())
                .compact();
    }

    // ── Validação ────────────────────────────────────────────────
    public boolean isTokenValido(String token, UsuarioModel usuario) {
        final String email = extrairEmail(token);
        return email.equals(usuario.getEmail()) && !isTokenExpirado(token);
    }

    public boolean isTokenExpirado(String token) {
        return extrairExpiracao(token).before(new Date());
    }

    // ── Extração de claims ───────────────────────────────────────
    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public UUID extrairId(String token) {
        return UUID.fromString(extrairClaim(token, c -> c.get("id", String.class)));
    }

    public Date extrairExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    public long getExpiracaoMs() {
        return expiracaoMs;
    }

    public <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extrairTodosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extrairTodosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}