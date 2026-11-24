package cl.suus.ms_ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BoletaRequest(
        @Schema(example = "Pedro")
        @NotBlank(message = "El nombre del cliente es obligatorio")
        @Size(max = 50)
        String nombre,

        @Schema(example = "González Cliente")
        @NotBlank(message = "Los apellidos del cliente son obligatorios")
        @Size(max = 100)
        String apellidos,

        @Schema(example = "pedro@gmail.com")
        @NotBlank(message = "El correo del cliente es obligatorio")
        @Email(message = "El formato del correo no es válido")
        @Size(max = 100)
        String correo,

        @Schema(example = "Av. Mapocho 4120")
        @NotBlank(message = "La calle de envío es obligatoria")
        @Size(max = 200)
        String calle,

        @Schema(example = "Depto 402")
        @Size(max = 50)
        String departamento,

        @Schema(example = "Región Metropolitana de Santiago")
        @NotBlank(message = "La región es obligatoria")
        @Size(max = 100)
        String region,

        @Schema(example = "Quinta Normal")
        @NotBlank(message = "La comuna es obligatoria")
        @Size(max = 100)
        String comuna,

        @Schema(example = "Dejar en conserjería")
        @Size(max = 300)
        String indicaciones,

        @NotEmpty(message = "Debe incluir al menos un producto en la compra")
        @Valid
        List<ItemCompraRequest> items
) {}