package es.unex.mdai.FitReserve.data.repository;

import es.unex.mdai.FitReserve.data.model.Actividad;
import es.unex.mdai.FitReserve.data.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala,Long> {

    Optional<Sala> findByIdEntrenador(Long idEntrenador);

    // Salas libres en un intervalo
    @Query("""
      SELECT s FROM Sala s
      WHERE NOT EXISTS (
        SELECT 1 FROM Reserva r
        WHERE r.sala = s
          AND r.estado IN :estadosOcupados
          AND r.fechaHoraInicio < :fin
          AND r.fechaHoraFin  > :inicio
      )
    """)
    List<Sala> findDisponibles(LocalDateTime inicio,
                               LocalDateTime fin,
                               @Param("estadosOcupados") Collection<String> estadosOcupados);

    List<Sala> findAll();

    void deleteByIdSala(Long idSala);
}


