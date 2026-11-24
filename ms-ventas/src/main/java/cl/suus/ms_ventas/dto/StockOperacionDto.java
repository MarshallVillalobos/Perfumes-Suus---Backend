package cl.suus.ms_ventas.dto;

import java.util.List;

public record StockOperacionDto(
        List<StockItemDto> items
) {}