package cl.suus.ms_ventas.repository;

import cl.suus.ms_ventas.model.Boleta;
import cl.suus.ms_ventas.model.EstadoBoleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    List<Boleta> findAllByOrderByFechaDesc();

    List<Boleta> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    @Query("SELECT COALESCE(SUM(b.total), 0) FROM Boleta b WHERE b.estado <> :estadoExcluido")
    Long sumarTotalExcluyendoEstado(@Param("estadoExcluido") EstadoBoleta estadoExcluido);

    long countByEstadoNot(EstadoBoleta estadoExcluido);
}