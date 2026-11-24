package cl.suus.ms_productos.dto;

import cl.suus.ms_productos.model.Producto;

public record ProductoResponse(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        String marca,
        Integer precio,
        Integer precioOferta,
        Integer precioFinal,
        boolean enOferta,
        Integer stock,
        Integer stockCritico,
        boolean critico,
        String imagenUrl,
        Long categoriaId,
        String categoriaNombre,
        String categoriaColor
) {
    public static ProductoResponse from(Producto p) {
        boolean enOferta = p.getPrecioOferta() != null && p.getPrecioOferta() < p.getPrecio();
        Integer precioFinal = enOferta ? p.getPrecioOferta() : p.getPrecio();
        boolean critico = p.getStockCritico() != null && p.getStock() <= p.getStockCritico();

        return new ProductoResponse(
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getDescripcion(),
                p.getMarca(),
                p.getPrecio(),
                p.getPrecioOferta(),
                precioFinal,
                enOferta,
                p.getStock(),
                p.getStockCritico(),
                critico,
                p.getImagenUrl(),
                p.getCategoria().getId(),
                p.getCategoria().getNombre(),
                p.getCategoria().getColor()
        );
    }
}