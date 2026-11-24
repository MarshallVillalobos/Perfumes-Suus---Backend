package cl.suus.ms_ventas.dto;

import cl.suus.ms_ventas.model.DetalleBoleta;

public record DetalleBoletaResponse(
        Long id,
        Long productoId,
        String nombreProducto,
        Integer cantidad,
        Integer precioUnitario,
        Integer subtotal
) {
    public static DetalleBoletaResponse from(DetalleBoleta d) {
        return new DetalleBoletaResponse(
                d.getId(),
                d.getProductoId(),
                d.getNombreProducto(),
                d.getCantidad(),
                d.getPrecioUnitario(),
                d.getSubtotal()
        );
    }
}