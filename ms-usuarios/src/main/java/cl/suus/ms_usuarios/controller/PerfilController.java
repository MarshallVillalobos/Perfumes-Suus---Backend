package cl.suus.ms_usuarios.controller;

import cl.suus.ms_usuarios.dto.PerfilUpdateRequest;
import cl.suus.ms_usuarios.dto.UsuarioResponse;
import cl.suus.ms_usuarios.security.UsuarioAutenticado;
import cl.suus.ms_usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/perfil")
@RequiredArgsConstructor
@Tag(name = "Perfil", description = "Consulta y edición de los datos del usuario autenticado")
public class PerfilController {

    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Ver perfil propio", description = "Acceso: Con sesión.")
    public ResponseEntity<UsuarioResponse> verPerfil(@AuthenticationPrincipal UsuarioAutenticado principal) {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(principal.correo()));
    }

    @PutMapping
    @Operation(summary = "Actualizar perfil propio", description = "Acceso: Con sesión. Permite editar datos personales y dirección.")
    public ResponseEntity<UsuarioResponse> actualizarPerfil(
            @AuthenticationPrincipal UsuarioAutenticado principal,
            @Valid @RequestBody PerfilUpdateRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(principal.correo(), request));
    }
}