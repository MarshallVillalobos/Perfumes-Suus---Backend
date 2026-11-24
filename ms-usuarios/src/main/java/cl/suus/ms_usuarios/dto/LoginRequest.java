package cl.suus.ms_usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(example = "admin@duoc.cl")
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El formato del correo no es válido")
        String correo,

        @Schema(example = "admin123")
        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}