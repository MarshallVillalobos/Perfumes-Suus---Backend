package cl.suus.ms_productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record ProductoRequest(
        @Schema(example = "PRD-005")
        @NotBlank(message = "El código del producto es obligatorio")
        @Size(min = 3, max = 50, message = "El código debe tener entre 3 y 50 caracteres")
        String codigo,

        @Schema(example = "Sauvage Elixir")
        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @Schema(example = "Fragancia especiada y amaderada de alta concentración")
        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String descripcion,

        @Schema(example = "Dior")
        @Size(max = 100, message = "La marca no puede superar los 100 caracteres")
        String marca,

        @Schema(example = "95000")
        @NotNull(message = "El precio es obligatorio")
        @Min(value = 0, message = "El precio no puede ser negativo")
        Integer precio,

        @Schema(example = "82000")
        @Min(value = 0, message = "El precio de oferta no puede ser negativo")
        Integer precioOferta,

        @Schema(example = "12")
        @NotNull(message = "El stock inicial es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        @Schema(example = "3")
        @Min(value = 0, message = "El stock crítico no puede ser negativo")
        Integer stockCritico,

        @Schema(example = "/img/Andrews-1-IA.webp")
        @Size(max = 300, message = "La URL de imagen no puede superar los 300 caracteres")
        String imagenUrl,

        @Schema(example = "1")
        @NotNull(message = "La categoría es obligatoria")
        Long categoriaId
) {}