package cl.suus.ms_usuarios.config;

import cl.suus.ms_usuarios.model.Rol;
import cl.suus.ms_usuarios.model.Usuario;
import cl.suus.ms_usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        usuarioRepository.saveAll(List.of(
                Usuario.builder()
                        .run("190110222")
                        .nombre("Susana")
                        .apellidos("Pérez Administradora")
                        .correo("admin@duoc.cl")
                        .password(passwordEncoder.encode("admin123"))
                        .rol(Rol.ADMIN)
                        .region("Región Metropolitana de Santiago")
                        .comuna("Santiago")
                        .direccion("Av. Libertador Bernardo O'Higgins 1449")
                        .fechaNacimiento(LocalDate.of(1995, 4, 12))
                        .build(),
                Usuario.builder()
                        .run("123456785")
                        .nombre("Camila")
                        .apellidos("Rojas Vendedora")
                        .correo("camila@duoc.cl")
                        .password(passwordEncoder.encode("camila123"))
                        .rol(Rol.VENDEDOR)
                        .region("Región Metropolitana de Santiago")
                        .comuna("Providencia")
                        .direccion("Av. Providencia 1234")
                        .fechaNacimiento(LocalDate.of(1998, 8, 20))
                        .build(),
                Usuario.builder()
                        .run("201234565")
                        .nombre("Pedro")
                        .apellidos("González Cliente")
                        .correo("pedro@gmail.com")
                        .password(passwordEncoder.encode("pedro123"))
                        .rol(Rol.CLIENTE)
                        .region("Región Metropolitana de Santiago")
                        .comuna("Quinta Normal")
                        .direccion("Av. Mapocho 4120")
                        .fechaNacimiento(LocalDate.of(2000, 11, 5))
                        .build()
        ));

        log.info("Datos iniciales cargados en suus_usuarios: 3 cuentas (ADMIN, VENDEDOR, CLIENTE).");
    }
}