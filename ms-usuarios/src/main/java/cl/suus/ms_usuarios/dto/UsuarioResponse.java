package cl.suus.ms_usuarios.dto;

import cl.suus.ms_usuarios.model.Rol;
import cl.suus.ms_usuarios.model.Usuario;

import java.time.LocalDate;

public record UsuarioResponse(
        Long id,
        String run,
        String nombre,
        String apellidos,
        String correo,
        Rol rol,
        String region,
        String comuna,
        String direccion,
        LocalDate fechaNacimiento
) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getRun(),
                u.getNombre(),
                u.getApellidos(),
                u.getCorreo(),
                u.getRol(),
                u.getRegion(),
                u.getComuna(),
                u.getDireccion(),
                u.getFechaNacimiento()
        );
    }
}