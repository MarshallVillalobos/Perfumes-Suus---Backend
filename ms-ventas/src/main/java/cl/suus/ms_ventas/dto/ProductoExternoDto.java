package cl.suus.ms_ventas.dto;

public record ProductoExternoDto(
        Long id,
        String codigo,
        String nombre,
        Integer precio,
        Integer precioOferta,
        Integer precioFinal,
        boolean enOferta,
        Integer stock
) {}