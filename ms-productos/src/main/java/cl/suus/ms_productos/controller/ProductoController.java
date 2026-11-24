package cl.suus.ms_productos.controller;

import cl.suus.ms_productos.dto.ProductoRequest;
import cl.suus.ms_productos.dto.ProductoResponse;
import cl.suus.ms_productos.dto.StockOperacionRequest;
import cl.suus.ms_productos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Catálogo de perfumes, ofertas, stock crítico y operaciones de inventario")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar catálogo de productos", description = "Acceso: Público. Permite filtrar por categoriaId y texto.")
    public ResponseEntity<List<ProductoResponse>> listar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String texto) {
        return ResponseEntity.ok(productoService.listar(categoriaId, texto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un producto", description = "Acceso: Público.")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @GetMapping("/ofertas")
    @Operation(summary = "Listar productos en oferta", description = "Acceso: Público. Productos con precio de oferta menor al precio normal.")
    public ResponseEntity<List<ProductoResponse>> listarOfertas() {
        return ResponseEntity.ok(productoService.listarOfertas());
    }

    @GetMapping("/criticos")
    @Operation(summary = "Listar productos con stock crítico", description = "Acceso: Solo ADMIN. Productos con stock igual o inferior al stock crítico.")
    public ResponseEntity<List<ProductoResponse>> listarCriticos() {
        return ResponseEntity.ok(productoService.listarCriticos());
    }

    @PostMapping
    @Operation(summary = "Crear producto", description = "Acceso: Solo ADMIN. Código único y oferta menor al precio normal.")
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Acceso: Solo ADMIN.")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Acceso: Solo ADMIN.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/stock/descontar")
    @Operation(summary = "Descontar stock (Interno)", description = "Acceso: Interno (usado por ms-ventas). Operación todo o nada.")
    public ResponseEntity descontarStock(@Valid @RequestBody StockOperacionRequest request) {
        productoService.descontarStock(request);
        return ResponseEntity.ok(Map.of("mensaje", "Stock descontado correctamente"));
    }

    @PostMapping("/stock/reponer")
    @Operation(summary = "Reponer stock (Interno)", description = "Acceso: Interno (usado por ms-ventas al anular boletas o compensar).")
    public ResponseEntity reponerStock(@Valid @RequestBody StockOperacionRequest request) {
        productoService.reponerStock(request);
        return ResponseEntity.ok(Map.of("mensaje", "Stock repuesto correctamente"));
    }
}