package es.unex.mdai.FitReserve.data.repository;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.Actividad;
import es.unex.mdai.FitReserve.data.model.Reserva;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva,Long> {
    // Listar para cliente/entrenador (próximas e historial)
    List<Reserva> findByClienteIdClienteOrderByFechaHoraInicioDesc(Long idCliente);
    List<Reserva> findByEntrenadorIdEntrenadorOrderByFechaHoraInicioDesc(Long idEntrenador);


    List<Reserva> findByClienteIdClienteAndFechaHoraInicioAfter(Long idCliente, LocalDateTime ahora);
    List<Reserva> findByEntrenadorIdEntrenadorAndFechaHoraInicioAfter(Long idEntrenador, LocalDateTime ahora);

    // Comprobar solapes → disponibilidad de sala/entrenador/reserva
    @Query("""
      SELECT COUNT(r) > 0
      FROM Reserva r
      WHERE r.sala.idSala = :salaId
        AND r.estado = :estadosOcupados
        AND r.fechaHoraInicio < :fin
        AND r.fechaHoraFin    > :inicio
    """)
    boolean existeSolapeSala(@Param("salaId") Long salaId,
                             @Param("inicio") LocalDateTime inicio,
                             @Param("fin") LocalDateTime fin,
                             @Param("estadosOcupados") Estado estadosOcupados);

    @Query("""
      SELECT COUNT(r) > 0
      FROM Reserva r
      WHERE r.entrenador.idEntrenador = :entrenadorId
        AND r.estado = :estadosOcupados
        AND r.fechaHoraInicio < :fin
        AND r.fechaHoraFin    > :inicio
    """)
    boolean existeSolapeEntrenador(@Param("entrenadorId") Long entrenadorId,
                                   @Param("inicio") LocalDateTime inicio,
                                   @Param("fin") LocalDateTime fin,
                                   @Param("estadosOcupados") Estado estadosOcupados);

    // Para “mi reserva actual o siguiente”
    @Query("""
      SELECT r FROM Reserva r
      WHERE r.cliente.idCliente = :clienteId
        AND r.fechaHoraInicio > :desde
      ORDER BY r.fechaHoraInicio ASC
    """)
    List<Reserva> proximaReservaCliente(Long clienteId, LocalDateTime desde, Pageable top1);

    @Query("""
      SELECT r FROM Reserva r
      WHERE r.entrenador.idEntrenador = :entrenadorId
        AND r.fechaHoraInicio > :desde
      ORDER BY r.fechaHoraInicio ASC
    """)
    List<Reserva> proximaClaseEntrenador(Long entrenadorId, LocalDateTime desde, Pageable top1);

    void deleteByIdReserva(Long idReserva);
}
