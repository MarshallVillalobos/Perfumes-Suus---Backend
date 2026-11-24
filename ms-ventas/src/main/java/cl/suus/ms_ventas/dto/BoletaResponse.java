package cl.suus.ms_ventas.dto;

import cl.suus.ms_ventas.model.Boleta;
import cl.suus.ms_ventas.model.EstadoBoleta;

import java.time.LocalDateTime;
import java.util.List;

public record BoletaResponse(
        Long id,
        LocalDateTime fecha,
        EstadoBoleta estado,
        Integer total,
        Long usuarioId,
        String clienteNombre,
        String clienteApellidos,
        String clienteCorreo,
        String calle,
        String departamento,
        String region,
        String comuna,
        String indicaciones,
        List<DetalleBoletaResponse> detalles
) {
    public static BoletaResponse from(Boleta b) {
        List<DetalleBoletaResponse> detallesDto = b.getDetalles()
                .stream()
                .map(DetalleBoletaResponse::from)
                .toList();

        return new BoletaResponse(
                b.getId(),
                b.getFecha(),
                b.getEstado(),
                b.getTotal(),
                b.getUsuarioId(),
                b.getClienteNombre(),
                b.getClienteApellidos(),
                b.getClienteCorreo(),
                b.getCalle(),
                b.getDepartamento(),
                b.getRegion(),
                b.getComuna(),
                b.getIndicaciones(),
                detallesDto
        );
    }
}