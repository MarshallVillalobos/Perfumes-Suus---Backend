package cl.suus.ms_usuarios.service;

import cl.suus.ms_usuarios.dto.*;
import cl.suus.ms_usuarios.exception.ConflictException;
import cl.suus.ms_usuarios.exception.ResourceNotFoundException;
import cl.suus.ms_usuarios.exception.UnauthorizedException;
import cl.suus.ms_usuarios.model.Rol;
import cl.suus.ms_usuarios.model.Usuario;
import cl.suus.ms_usuarios.repository.UsuarioRepository;
import cl.suus.ms_usuarios.security.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RunValidator runValidator;

    public AuthResponse login(LoginRequest request) {
        String correo = request.correo().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new UnauthorizedException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new UnauthorizedException("Credenciales incorrectas");
        }

        log.info("Inicio de sesión exitoso: correo={}, rol={}", usuario.getCorreo(), usuario.getRol());
        return construirRespuestaAuth(usuario);
    }

    @Transactional
    public AuthResponse registro(RegistroRequest request) {
        String runValido = runValidator.normalizarYValidar(request.run());
        String correo = request.correo().trim().toLowerCase();

        if (usuarioRepository.existsByRunIgnoreCase(runValido)) {
            throw new ConflictException("Ya existe un usuario registrado con ese RUN");
        }
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictException("Ya existe un usuario registrado con ese correo");
        }

        Usuario nuevo = Usuario.builder()
                .run(runValido)
                .nombre(request.nombre().trim())
                .apellidos(request.apellidos().trim())
                .correo(correo)
                .password(passwordEncoder.encode(request.password()))
                .rol(Rol.CLIENTE) // Regla obligatoria: registro público siempre es CLIENTE
                .region(request.region())
                .comuna(request.comuna())
                .direccion(request.direccion())
                .fechaNacimiento(request.fechaNacimiento())
                .build();

        Usuario guardado = usuarioRepository.save(nuevo);
        log.info("Cliente registrado: id={}, correo={}", guardado.getId(), guardado.getCorreo());
        return construirRespuestaAuth(guardado);
    }

    public AuthResponse refresh(RefreshRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtService.esTokenValidoDeTipo(refreshToken, JwtService.TIPO_REFRESH)) {
            throw new UnauthorizedException("Refresh token inválido, vencido o de tipo incorrecto");
        }

        Claims claims = jwtService.extraerClaims(refreshToken);
        String correo = claims.getSubject();

        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new UnauthorizedException("Usuario asociado al token no existe"));

        return construirRespuestaAuth(usuario);
    }

    public UsuarioResponse obtenerUsuarioActual(String correo) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return UsuarioResponse.from(usuario);
    }

    private AuthResponse construirRespuestaAuth(Usuario usuario) {
        String accessToken = jwtService.generarAccessToken(usuario);
        String refreshToken = jwtService.generarRefreshToken(usuario);
        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessExpirationMs(),
                UsuarioResponse.from(usuario)
        );
    }
}