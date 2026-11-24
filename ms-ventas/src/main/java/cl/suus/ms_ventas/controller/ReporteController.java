package cl.suus.ms_ventas.controller;

import cl.suus.ms_ventas.dto.ResumenVentasResponse;
import cl.suus.ms_ventas.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Indicadores y estadísticas de ventas (excluye boletas anuladas)")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/resumen")
    @Operation(summary = "Obtener resumen de ventas", description = "Acceso: Solo ADMIN. Total vendido, cantidad de boletas, ticket promedio y perfumes más vendidos.")
    public ResponseEntity<ResumenVentasResponse> obtenerResumen() {
        return ResponseEntity.ok(reporteService.obtenerResumen());
    }
}