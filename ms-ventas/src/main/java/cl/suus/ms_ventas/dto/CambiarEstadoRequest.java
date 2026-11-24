package cl.suus.ms_ventas.dto;

import cl.suus.ms_ventas.model.EstadoBoleta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoRequest(
        @Schema(example = "ENVIADA")
        @NotNull(message = "El nuevo estado es obligatorio")
        EstadoBoleta estado
) {}