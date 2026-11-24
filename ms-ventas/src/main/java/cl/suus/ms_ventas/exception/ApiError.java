package cl.suus.ms_ventas.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        int codigo,
        String mensaje,
        LocalDateTime timestamp,
        Map<String, String> errores
) {
    public static ApiError of(int codigo, String mensaje) {
        return new ApiError(codigo, mensaje, LocalDateTime.now(), null);
    }

    public static ApiError of(int codigo, String mensaje, Map<String, String> errores) {
        return new ApiError(codigo, mensaje, LocalDateTime.now(), errores);
    }
}