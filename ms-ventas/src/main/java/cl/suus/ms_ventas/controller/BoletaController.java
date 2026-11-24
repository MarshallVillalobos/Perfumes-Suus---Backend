package cl.suus.ms_ventas.controller;

import cl.suus.ms_ventas.dto.BoletaRequest;
import cl.suus.ms_ventas.dto.BoletaResponse;
import cl.suus.ms_ventas.dto.CambiarEstadoRequest;
import cl.suus.ms_ventas.security.UsuarioAutenticado;
import cl.suus.ms_ventas.service.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boletas")
@RequiredArgsConstructor
@Tag(name = "Boletas", description = "Registro de compras, historial y gestión de estados de boletas")
public class BoletaController {

    private final BoletaService boletaService;

    @PostMapping
    @Operation(summary = "Registrar una compra", description = "Acceso: Público. Si hay sesión JWT se asocia al usuario; consulta precios y descuenta stock en ms-productos.")
    public ResponseEntity<BoletaResponse> crear(
            @Valid @RequestBody BoletaRequest request,
            @AuthenticationPrincipal UsuarioAutenticado principal) {
        Long usuarioId = (principal != null) ? principal.id() : null;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boletaService.registrarCompra(request, usuarioId));
    }

    @GetMapping("/mias")
    @Operation(summary = "Ver mis compras", description = "Acceso: Con sesión. Devuelve el historial de boletas del usuario autenticado.")
    public ResponseEntity<List<BoletaResponse>> misCompras(@AuthenticationPrincipal UsuarioAutenticado principal) {
        return ResponseEntity.ok(boletaService.listarPorUsuario(principal.id()));
    }

    @GetMapping
    @Operation(summary = "Listar todas las boletas", description = "Acceso: ADMIN y VENDEDOR.")
    public ResponseEntity<List<BoletaResponse>> listarTodas() {
        return ResponseEntity.ok(boletaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver detalle de una boleta", description = "Acceso: ADMIN y VENDEDOR.")
    public ResponseEntity<BoletaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de una boleta", description = "Acceso: Solo ADMIN. Si se cambia a ANULADA, repone el stock en ms-productos.")
    public ResponseEntity<BoletaResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request) {
        return ResponseEntity.ok(boletaService.cambiarEstado(id, request.estado()));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Ver historial de compras de un usuario", description = "Acceso: Solo ADMIN.")
    public ResponseEntity<List<BoletaResponse>> historialUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(boletaService.listarPorUsuario(usuarioId));
    }
}