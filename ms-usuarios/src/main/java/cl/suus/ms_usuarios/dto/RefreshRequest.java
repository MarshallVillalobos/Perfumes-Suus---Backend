package cl.suus.ms_usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @Schema(example = "eyJhbGciOiJIUzI1NiJ9...")
        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken
) {}