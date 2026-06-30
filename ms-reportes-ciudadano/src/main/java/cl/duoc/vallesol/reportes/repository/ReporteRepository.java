package cl.duoc.vallesol.reportes.repository;

import cl.duoc.vallesol.reportes.domain.EstadoReporte;
import cl.duoc.vallesol.reportes.domain.Gravedad;
import cl.duoc.vallesol.reportes.domain.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByEstado(EstadoReporte estado);

    List<Reporte> findByGravedad(Gravedad gravedad);

    long countByGravedad(Gravedad gravedad);

    List<Reporte> findTop10ByOrderByFechaCreacionDesc();
}
