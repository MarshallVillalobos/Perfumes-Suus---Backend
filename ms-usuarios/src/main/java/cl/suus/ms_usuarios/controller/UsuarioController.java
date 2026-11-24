package cl.suus.ms_usuarios.controller;

import cl.suus.ms_usuarios.dto.UsuarioAdminRequest;
import cl.suus.ms_usuarios.dto.UsuarioResponse;
import cl.suus.ms_usuarios.security.UsuarioAutenticado;
import cl.suus.ms_usuarios.service.UsuarioService;
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
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios (Admin)", description = "Mantenedor administrativo de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Acceso: Solo ADMIN.")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Acceso: Solo ADMIN.")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Acceso: Solo ADMIN. Permite asignar cualquier rol.")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Acceso: Solo ADMIN. Si password viene vacía, se conserva la actual.")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody UsuarioAdminRequest request) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Acceso: Solo ADMIN. Impide autoeliminación (409).")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                         @AuthenticationPrincipal UsuarioAutenticado principal) {
        usuarioService.eliminar(id, principal.id());
        return ResponseEntity.noContent().build();
    }
}