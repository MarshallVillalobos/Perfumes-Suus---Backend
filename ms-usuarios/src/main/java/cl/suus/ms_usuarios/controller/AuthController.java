package cl.suus.ms_usuarios.controller;

import cl.suus.ms_usuarios.dto.*;
import cl.suus.ms_usuarios.security.UsuarioAutenticado;
import cl.suus.ms_usuarios.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login, registro de clientes, renovación de tokens JWT y sesión actual")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Acceso: Público. Devuelve accessToken y refreshToken.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo cliente", description = "Acceso: Público. Siempre asigna el rol CLIENTE.")
    public ResponseEntity<AuthResponse> registro(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registro(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar tokens", description = "Acceso: Público. Solo acepta refreshToken válido y emite un nuevo par de tokens.")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener usuario autenticado", description = "Acceso: Con sesión. Devuelve los datos del dueño del token.")
    public ResponseEntity<UsuarioResponse> me(@AuthenticationPrincipal UsuarioAutenticado principal) {
        return ResponseEntity.ok(authService.obtenerUsuarioActual(principal.correo()));
    }
}