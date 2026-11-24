package cl.suus.ms_usuarios.security;

import cl.suus.ms_usuarios.model.Rol;

public record UsuarioAutenticado(
        Long id,
        String correo,
        Rol rol
) {}