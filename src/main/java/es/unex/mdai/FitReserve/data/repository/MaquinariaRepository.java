package es.unex.mdai.FitReserve.data.repository;

import es.unex.mdai.FitReserve.data.model.Actividad;
import es.unex.mdai.FitReserve.data.model.Maquinaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaquinariaRepository extends JpaRepository<Maquinaria,Long> {

    Optional<Maquinaria> findByIdMaquinaria(Long id);

    // Consultar maquinaria disponible entre dos fechas
    @Query("""
        SELECT m FROM Maquinaria m
        WHERE (m.cantidadTotal -
               COALESCE(
                   (SELECT SUM(rm.cantidad)
                    FROM ReservaMaquinaria rm
                    JOIN rm.reserva r
                    WHERE rm.maquinaria = m
                      AND r.estado IN :estadosActivos
                      AND r.fechaHoraInicio < :fin
                      AND r.fechaHoraFin > :inicio), 0)
              ) > 0
    """)
    List<Maquinaria> findDisponibles(@Param("inicio") LocalDateTime inicio,
                                     @Param("fin") LocalDateTime fin,
                                     @Param("estadosActivos") Collection<String> estadosActivos);

    List<Maquinaria> findAll();

    void deleteByIdMaquinaria(Long idMaquinaria);

}
