package cl.suus.ms_ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemCompraRequest(
        @Schema(example = "1")
        @NotNull(message = "El ID del producto es obligatorio")
        Long productoId,

        @Schema(example = "2")
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad mínima es 1")
        Integer cantidad
) {}