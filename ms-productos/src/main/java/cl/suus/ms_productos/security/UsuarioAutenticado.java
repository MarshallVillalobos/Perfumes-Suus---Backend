package cl.suus.ms_productos.security;

public record UsuarioAutenticado(
        Long id,
        String correo,
        String rol
) {}