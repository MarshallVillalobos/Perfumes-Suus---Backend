package cl.suus.ms_usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegistroRequest(
        @Schema(example = "190110222")
        @NotBlank(message = "El RUN es obligatorio")
        @Size(min = 7, max = 9, message = "El RUN debe tener entre 7 y 9 caracteres sin puntos ni guion")
        String run,

        @Schema(example = "Vicente")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        String nombre,

        @Schema(example = "Pérez Soto")
        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
        String apellidos,

        @Schema(example = "vicente@gmail.com")
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El formato del correo no es válido")
        @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
        String correo,

        @Schema(example = "clave1234")
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 4, max = 50, message = "La contraseña debe tener entre 4 y 50 caracteres")
        String password,

        @Schema(example = "Región Metropolitana de Santiago")
        @Size(max = 100)
        String region,

        @Schema(example = "Quinta Normal")
        @Size(max = 100)
        String comuna,

        @Schema(example = "Av. Mapocho 4120")
        @Size(max = 200)
        String direccion,

        @Schema(example = "2001-05-14")
        LocalDate fechaNacimiento
) {}