package cl.suus.ms_usuarios.service;

import cl.suus.ms_usuarios.dto.AuthResponse;
import cl.suus.ms_usuarios.dto.LoginRequest;
import cl.suus.ms_usuarios.dto.RefreshRequest;
import cl.suus.ms_usuarios.dto.RegistroRequest;
import cl.suus.ms_usuarios.exception.ConflictException;
import cl.suus.ms_usuarios.exception.UnauthorizedException;
import cl.suus.ms_usuarios.model.Rol;
import cl.suus.ms_usuarios.model.Usuario;
import cl.suus.ms_usuarios.repository.UsuarioRepository;
import cl.suus.ms_usuarios.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RunValidator runValidator;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_retornaTokensYUsuario_cuandoCredencialesSonValidas() {
        // Arrange
        Usuario admin = Usuario.builder()
                .id(1L).run("190110222").nombre("Susana").apellidos("Pérez")
                .correo("admin@duoc.cl").password("$2a$10$hash").rol(Rol.ADMIN)
                .build();

        when(usuarioRepository.findByCorreoIgnoreCase("admin@duoc.cl")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("admin123", "$2a$10$hash")).thenReturn(true);
        when(jwtService.generarAccessToken(admin)).thenReturn("access-jwt");
        when(jwtService.generarRefreshToken(admin)).thenReturn("refresh-jwt");
        when(jwtService.getAccessExpirationMs()).thenReturn(900_000L);

        // Act
        AuthResponse response = authService.login(new LoginRequest("admin@duoc.cl", "admin123"));

        // Assert
        assertNotNull(response);
        assertEquals("access-jwt", response.accessToken());
        assertEquals("refresh-jwt", response.refreshToken());
        assertEquals(Rol.ADMIN, response.usuario().rol());
    }

    @Test
    void login_lanzaUnauthorizedException_cuandoPasswordEsIncorrecta() {
        // Arrange
        Usuario admin = Usuario.builder()
                .id(1L).correo("admin@duoc.cl").password("$2a$10$hash").rol(Rol.ADMIN).build();

        when(usuarioRepository.findByCorreoIgnoreCase("admin@duoc.cl")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("claveMala", "$2a$10$hash")).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("admin@duoc.cl", "claveMala")));
        verify(jwtService, never()).generarAccessToken(any());
    }

    @Test
    void registro_asignaRolClienteYEncriptaPassword() {
        // Arrange
        RegistroRequest request = new RegistroRequest(
                "201234565", "Pedro", "González", "pedro@gmail.com",
                "pedro123", "RM", "Quinta Normal", "Av. Mapocho 4120", null
        );

        when(runValidator.normalizarYValidar("201234565")).thenReturn("201234565");
        when(usuarioRepository.existsByRunIgnoreCase("201234565")).thenReturn(false);
        when(usuarioRepository.existsByCorreoIgnoreCase("pedro@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("pedro123")).thenReturn("$2a$10$passwordEncriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(5L);
            return u;
        });
        when(jwtService.generarAccessToken(any(Usuario.class))).thenReturn("access-jwt");
        when(jwtService.generarRefreshToken(any(Usuario.class))).thenReturn("refresh-jwt");
        when(jwtService.getAccessExpirationMs()).thenReturn(900_000L);

        // Act
        AuthResponse response = authService.registro(request);

        // Assert
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        assertEquals(Rol.CLIENTE, captor.getValue().getRol());
        assertEquals("$2a$10$passwordEncriptada", captor.getValue().getPassword());
        assertEquals(Rol.CLIENTE, response.usuario().rol());
    }

    @Test
    void registro_lanzaConflictException_cuandoCorreoYaExiste() {
        // Arrange
        RegistroRequest request = new RegistroRequest(
                "201234565", "Pedro", "González", "admin@duoc.cl",
                "pedro123", "RM", "Santiago", "Calle 1", null
        );

        when(runValidator.normalizarYValidar("201234565")).thenReturn("201234565");
        when(usuarioRepository.existsByRunIgnoreCase("201234565")).thenReturn(false);
        when(usuarioRepository.existsByCorreoIgnoreCase("admin@duoc.cl")).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> authService.registro(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void refresh_lanzaUnauthorizedException_cuandoTokenNoEsTipoRefresh() {
        // Arrange
        when(jwtService.esTokenValidoDeTipo("solo-access-token", JwtService.TIPO_REFRESH)).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class,
                () -> authService.refresh(new RefreshRequest("solo-access-token")));
        verify(usuarioRepository, never()).findByCorreoIgnoreCase(any());
    }
}