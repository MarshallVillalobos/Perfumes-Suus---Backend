package cl.suus.ms_usuarios.security;

import cl.suus.ms_usuarios.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    public static final String TIPO_ACCESS = "ACCESS";
    public static final String TIPO_REFRESH = "REFRESH";

    private final SecretKey key;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-expiration-ms:900000}") long accessExpirationMs,
            @Value("${app.jwt.refresh-expiration-ms:604800000}") long refreshExpirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generarAccessToken(Usuario usuario) {
        return construirToken(usuario, TIPO_ACCESS, accessExpirationMs);
    }

    public String generarRefreshToken(Usuario usuario) {
        return construirToken(usuario, TIPO_REFRESH, refreshExpirationMs);
    }

    public long getAccessExpirationMs() {
        return accessExpirationMs;
    }

    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean esTokenValidoDeTipo(String token, String tipoEsperado) {
        try {
            Claims claims = extraerClaims(token);
            String tipo = claims.get("tipo", String.class);
            return tipoEsperado.equals(tipo);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public Long extraerId(Claims claims) {
        Number id = claims.get("id", Number.class);
        return id != null ? id.longValue() : null;
    }

    public String extraerRol(Claims claims) {
        return claims.get("rol", String.class);
    }

    private String construirToken(Usuario usuario, String tipo, long duracionMs) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + duracionMs);

        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim("id", usuario.getId())
                .claim("rol", usuario.getRol().name())
                .claim("tipo", tipo)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(key)
                .compact();
    }
}