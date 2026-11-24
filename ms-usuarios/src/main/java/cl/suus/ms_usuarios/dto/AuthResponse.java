package cl.suus.ms_usuarios.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiraEnMs,
        UsuarioResponse usuario
) {}