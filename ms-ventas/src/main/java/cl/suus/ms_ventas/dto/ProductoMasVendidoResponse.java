package cl.suus.ms_ventas.dto;

public record ProductoMasVendidoResponse(
        Long productoId,
        String nombreProducto,
        Long cantidadVendida,
        Long totalVendido
) {}