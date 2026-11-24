package cl.suus.ms_usuarios.repository;

import cl.suus.ms_usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findAllByOrderByIdAsc();

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    Optional<Usuario> findByRunIgnoreCase(String run);

    boolean existsByRunIgnoreCase(String run);
}