package cl.suus.ms_ventas.security;

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
    private static final long INTERNAL_TOKEN_EXPIRATION_MS = 60_000L; // 1 minuto

    private final SecretKey key;

    public JwtService(@Value("${app.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean esAccessTokenValido(String token) {
        try {
            Claims claims = extraerClaims(token);
            return TIPO_ACCESS.equals(claims.get("tipo", String.class));
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

    /**
     * Genera un token interno de corta duración firmado con el secreto compartido
     * para autenticar las llamadas de stock desde ms-ventas hacia ms-productos.
     */
    public String generarTokenInterno() {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + INTERNAL_TOKEN_EXPIRATION_MS);

        return Jwts.builder()
                .subject("ms-ventas")
                .claim("id", 0L)
                .claim("rol", "INTERNO")
                .claim("tipo", TIPO_ACCESS)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(key)
                .compact();
    }
}