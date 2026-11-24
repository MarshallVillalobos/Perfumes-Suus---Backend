package cl.suus.ms_productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @Schema(example = "Perfumes Árabes")
        @NotBlank(message = "El nombre de la categoría es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @Schema(example = "Fragancias intensas de larga duración con notas orientales y amaderadas")
        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String descripcion,

        @Schema(example = "#5C3A21")
        @Size(max = 30, message = "El color no puede superar los 30 caracteres")
        String color
) {}