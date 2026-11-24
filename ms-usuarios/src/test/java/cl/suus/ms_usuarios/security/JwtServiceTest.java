package cl.suus.ms_usuarios.security;

import cl.suus.ms_usuarios.model.Rol;
import cl.suus.ms_usuarios.model.Usuario;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "perfumes-suus-clave-secreta-jwt-2026-minimo-32-caracteres";
    private JwtService jwtService;
    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 900_000L, 604_800_000L);
        usuarioPrueba = Usuario.builder()
                .id(1L)
                .correo("admin@duoc.cl")
                .rol(Rol.ADMIN)
                .build();
    }

    @Test
    void generarAccessToken_esValidoYContieneClaimsDelUsuario() {
        String token = jwtService.generarAccessToken(usuarioPrueba);

        assertTrue(jwtService.esTokenValidoDeTipo(token, JwtService.TIPO_ACCESS));
        Claims claims = jwtService.extraerClaims(token);
        assertEquals("admin@duoc.cl", claims.getSubject());
        assertEquals(1L, jwtService.extraerId(claims));
        assertEquals("ADMIN", jwtService.extraerRol(claims));
    }

    @Test
    void esTokenValidoDeTipo_distingueCorrectamenteEntreAccessYRefresh() {
        String accessToken = jwtService.generarAccessToken(usuarioPrueba);
        String refreshToken = jwtService.generarRefreshToken(usuarioPrueba);

        assertFalse(jwtService.esTokenValidoDeTipo(accessToken, JwtService.TIPO_REFRESH));
        assertFalse(jwtService.esTokenValidoDeTipo(refreshToken, JwtService.TIPO_ACCESS));
        assertTrue(jwtService.esTokenValidoDeTipo(refreshToken, JwtService.TIPO_REFRESH));
    }

    @Test
    void esTokenValidoDeTipo_retornaFalse_cuandoTokenEstaVencido() {
        JwtService jwtExpirado = new JwtService(SECRET, -1_000L, -1_000L);
        String tokenVencido = jwtExpirado.generarAccessToken(usuarioPrueba);

        assertFalse(jwtService.esTokenValidoDeTipo(tokenVencido, JwtService.TIPO_ACCESS));
    }
}