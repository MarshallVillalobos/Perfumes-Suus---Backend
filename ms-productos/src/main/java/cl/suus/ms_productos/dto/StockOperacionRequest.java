package cl.suus.ms_productos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record StockOperacionRequest(
        @NotEmpty(message = "Debe enviar al menos un producto")
        @Valid
        List<StockItemRequest> items
) {}