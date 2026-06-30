package cl.duoc.vallesol.alertas.repository;

import cl.duoc.vallesol.alertas.domain.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByActivaTrueOrderByFechaEmisionDesc();

    long countByActivaTrue();
}
