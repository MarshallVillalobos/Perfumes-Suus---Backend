package cl.suus.ms_ventas.security;

public record UsuarioAutenticado(
        Long id,
        String correo,
        String rol
) {}