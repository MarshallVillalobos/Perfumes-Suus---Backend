package cl.suus.ms_productos.dto;

import cl.suus.ms_productos.model.Categoria;

public record CategoriaResponse(
        Long id,
        String nombre,
        String descripcion,
        String color
) {
    public static CategoriaResponse from(Categoria c) {
        return new CategoriaResponse(
                c.getId(),
                c.getNombre(),
                c.getDescripcion(),
                c.getColor()
        );
    }
}