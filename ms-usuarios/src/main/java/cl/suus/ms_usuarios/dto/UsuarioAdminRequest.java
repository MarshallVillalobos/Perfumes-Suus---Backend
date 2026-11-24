package cl.suus.ms_usuarios.dto;

import cl.suus.ms_usuarios.model.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UsuarioAdminRequest(
        @Schema(example = "123456785")
        @NotBlank(message = "El RUN es obligatorio")
        @Size(min = 7, max = 9, message = "El RUN debe tener entre 7 y 9 caracteres sin puntos ni guion")
        String run,

        @Schema(example = "Camila")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        String nombre,

        @Schema(example = "Rojas Díaz")
        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
        String apellidos,

        @Schema(example = "camila@duoc.cl")
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El formato del correo no es válido")
        @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
        String correo,

        @Schema(example = "camila123", description = "Obligatoria al crear; opcional al editar (si viene vacía no se cambia)")
        @Size(max = 50, message = "La contraseña no puede superar los 50 caracteres")
        String password,

        @Schema(example = "VENDEDOR")
        @NotNull(message = "El rol es obligatorio")
        Rol rol,

        @Schema(example = "Región Metropolitana de Santiago")
        @Size(max = 100)
        String region,

        @Schema(example = "Providencia")
        @Size(max = 100)
        String comuna,

        @Schema(example = "Av. Providencia 1234")
        @Size(max = 200)
        String direccion,

        @Schema(example = "1998-08-20")
        LocalDate fechaNacimiento
) {}