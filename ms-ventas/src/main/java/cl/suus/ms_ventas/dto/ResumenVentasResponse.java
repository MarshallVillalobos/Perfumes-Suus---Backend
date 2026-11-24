package cl.suus.ms_ventas.dto;

import java.util.List;

public record ResumenVentasResponse(
        Long totalVendido,
        Long cantidadBoletas,
        Long ticketPromedio,
        List<ProductoMasVendidoResponse> masVendidos
) {}