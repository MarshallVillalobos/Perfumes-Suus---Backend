package cl.suus.ms_usuarios.service;

import cl.suus.ms_usuarios.dto.PerfilUpdateRequest;
import cl.suus.ms_usuarios.dto.UsuarioAdminRequest;
import cl.suus.ms_usuarios.dto.UsuarioResponse;
import cl.suus.ms_usuarios.exception.BadRequestException;
import cl.suus.ms_usuarios.exception.ConflictException;
import cl.suus.ms_usuarios.exception.ResourceNotFoundException;
import cl.suus.ms_usuarios.model.Usuario;
import cl.suus.ms_usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RunValidator runValidator;

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAllByOrderByIdAsc()
                .stream()
                .map(UsuarioResponse::from)
                .toList();
    }

    public UsuarioResponse obtenerPorId(Long id) {
        return UsuarioResponse.from(buscarEntidad(id));
    }

    public UsuarioResponse obtenerPerfil(String correo) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse actualizarPerfil(String correo, PerfilUpdateRequest request) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setNombre(request.nombre().trim());
        usuario.setApellidos(request.apellidos().trim());
        usuario.setRegion(request.region());
        usuario.setComuna(request.comuna());
        usuario.setDireccion(request.direccion());
        usuario.setFechaNacimiento(request.fechaNacimiento());

        if (request.password() != null && !request.password().isBlank()) {
            validarLargoPassword(request.password());
            usuario.setPassword(passwordEncoder.encode(request.password()));
        }

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioAdminRequest request) {
        String runValido = runValidator.normalizarYValidar(request.run());
        String correo = request.correo().trim().toLowerCase();

        if (request.password() == null || request.password().isBlank()) {
            throw new BadRequestException("La contraseña es obligatoria al crear un usuario");
        }
        validarLargoPassword(request.password());

        if (usuarioRepository.existsByRunIgnoreCase(runValido)) {
            throw new ConflictException("Ya existe un usuario con el RUN: " + runValido);
        }
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictException("Ya existe un usuario con el correo: " + correo);
        }

        Usuario nuevo = Usuario.builder()
                .run(runValido)
                .nombre(request.nombre().trim())
                .apellidos(request.apellidos().trim())
                .correo(correo)
                .password(passwordEncoder.encode(request.password()))
                .rol(request.rol())
                .region(request.region())
                .comuna(request.comuna())
                .direccion(request.direccion())
                .fechaNacimiento(request.fechaNacimiento())
                .build();

        Usuario guardado = usuarioRepository.save(nuevo);
        log.info("Usuario creado por ADMIN: id={}, correo={}, rol={}", guardado.getId(), guardado.getCorreo(), guardado.getRol());
        return UsuarioResponse.from(guardado);
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioAdminRequest request) {
        Usuario usuario = buscarEntidad(id);
        String runValido = runValidator.normalizarYValidar(request.run());
        String correo = request.correo().trim().toLowerCase();

        usuarioRepository.findByRunIgnoreCase(runValido)
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new ConflictException("Ya existe otro usuario con el RUN: " + runValido);
                });

        usuarioRepository.findByCorreoIgnoreCase(correo)
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new ConflictException("Ya existe otro usuario con el correo: " + correo);
                });

        usuario.setRun(runValido);
        usuario.setNombre(request.nombre().trim());
        usuario.setApellidos(request.apellidos().trim());
        usuario.setCorreo(correo);
        usuario.setRol(request.rol());
        usuario.setRegion(request.region());
        usuario.setComuna(request.comuna());
        usuario.setDireccion(request.direccion());
        usuario.setFechaNacimiento(request.fechaNacimiento());

        // Si la contraseña viene vacía, no se modifica
        if (request.password() != null && !request.password().isBlank()) {
            validarLargoPassword(request.password());
            usuario.setPassword(passwordEncoder.encode(request.password()));
        }

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminar(Long id, Long idAdminActual) {
        if (id.equals(idAdminActual)) {
            throw new ConflictException("Un administrador no puede eliminarse a sí mismo");
        }
        Usuario usuario = buscarEntidad(id);
        usuarioRepository.delete(usuario);
        log.info("Usuario eliminado por ADMIN: id={}", id);
    }

    private void validarLargoPassword(String password) {
        if (password.length() < 4 || password.length() > 50) {
            throw new BadRequestException("La contraseña debe tener entre 4 y 50 caracteres");
        }
    }

    private Usuario buscarEntidad(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }
}