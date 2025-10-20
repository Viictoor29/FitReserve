package es.unex.mdai.FitReserve.data.repository;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.ReservaMaquinaria;
import es.unex.mdai.FitReserve.data.model.ReservaMaquinariaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ReservaMaquinariaRepository extends JpaRepository<ReservaMaquinaria, ReservaMaquinariaId> {

    List<ReservaMaquinaria> findByReservaIdReserva(Long idReserva);
    void deleteByReservaIdReserva(Long idReserva);

    @Query("""
        SELECT COALESCE(SUM(rm.cantidad), 0)
        FROM ReservaMaquinaria rm
        JOIN rm.reserva r
        WHERE rm.maquinaria.idMaquinaria = :maquinariaId
          AND r.estado IN :estadosActivos
          AND r.fechaHoraInicio < :fin
          AND r.fechaHoraFin > :inicio
    """)
    Integer totalReservadoEnIntervalo(@Param("maquinariaId") Long maquinariaId,
                                      @Param("inicio") LocalDateTime inicio,
                                      @Param("fin") LocalDateTime fin,
                                      @Param("estadosActivos") Collection<Estado> estadosActivos);
}

