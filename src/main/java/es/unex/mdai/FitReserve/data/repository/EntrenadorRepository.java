package es.unex.mdai.FitReserve.data.repository;

import es.unex.mdai.FitReserve.data.model.Actividad;
import es.unex.mdai.FitReserve.data.model.Entrenador;
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
public interface EntrenadorRepository extends JpaRepository<Entrenador,Long> {

    Optional<Entrenador> findByUsuarioIdUsuario(Long idUsuario);

    Optional<Entrenador> findByIdEntrenador(Long idEntrenador);

    // Entrenadores disponibles en un intervalo (y opcional por especialidad)
    @Query("""
      SELECT e FROM Entrenador e
      WHERE (:especialidad IS NULL OR e.especialidad = :especialidad)
        AND NOT EXISTS (
          SELECT 1 FROM Reserva r
          WHERE r.entrenador = e
            AND r.estado IN :estadosOcupados
            AND r.fechaHoraInicio < :fin
            AND r.fechaHoraFin  > :inicio
        )
    """)
    List<Entrenador> findDisponibles(LocalDateTime inicio,
                                     LocalDateTime fin,
                                     @Param("estadosOcupados") Collection<String> estadosOcupados,
                                     @Param("especialidad") String especialidad);

    List<Entrenador> findByEspecialidad(String especialidad);
}
