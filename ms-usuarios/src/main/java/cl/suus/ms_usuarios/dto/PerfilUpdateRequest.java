package cl.suus.ms_usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PerfilUpdateRequest(
        @Schema(example = "Pedro")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        String nombre,

        @Schema(example = "González Muñoz")
        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
        String apellidos,

        @Schema(example = "Región Metropolitana de Santiago")
        @Size(max = 100)
        String region,

        @Schema(example = "Santiago")
        @Size(max = 100)
        String comuna,

        @Schema(example = "Alameda 2345")
        @Size(max = 200)
        String direccion,

        @Schema(example = "1999-03-10")
        LocalDate fechaNacimiento,

        @Schema(example = "", description = "Opcional: si se envía vacía, se mantiene la contraseña actual")
        @Size(max = 50, message = "La contraseña no puede superar los 50 caracteres")
        String password
) {}