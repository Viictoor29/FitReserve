package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Maquinaria;
import es.unex.mdai.FitReserve.data.model.Reserva;
import es.unex.mdai.FitReserve.data.model.ReservaMaquinaria;
import es.unex.mdai.FitReserve.data.repository.EntrenadorRepository;
import es.unex.mdai.FitReserve.data.repository.MaquinariaRepository;
import es.unex.mdai.FitReserve.data.repository.ReservaMaquinariaRepository;
import es.unex.mdai.FitReserve.data.repository.ReservaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ReservaServicioImpl implements  ReservaServicio {

    private final ReservaRepository reservaRepository;
    private final ReservaMaquinariaRepository reservaMaquinariaRepository;
    private final MaquinariaRepository maquinariaRepository;
    private final EntrenadorRepository entrenadorRepository;

    public ReservaServicioImpl(ReservaRepository reservaRepository,
                               ReservaMaquinariaRepository reservaMaquinariaRepository,
                               MaquinariaRepository maquinariaRepository, EntrenadorRepository entrenadorRepository) {
        this.reservaRepository = reservaRepository;
        this.reservaMaquinariaRepository = reservaMaquinariaRepository;
        this.maquinariaRepository = maquinariaRepository;
        this.entrenadorRepository = entrenadorRepository;
    }

    @Override
    public boolean crearReserva(Reserva reserva) {
        if (reserva == null ||
                reserva.getFechaHoraInicio() == null ||
                reserva.getFechaHoraFin() == null ||
                reserva.getSala() == null ||
                reserva.getEntrenador() == null ||
                reserva.getCliente() == null ||
                reserva.getActividad() == null) {
            return false;
        }

        LocalDateTime inicio = reserva.getFechaHoraInicio();
        LocalDateTime fin = reserva.getFechaHoraFin();

        // Validación básica de fechas
        if (!fin.isAfter(inicio)) {
            return false;
        }

        // No permitir crear reservas en el pasado
        if (inicio.isBefore(LocalDateTime.now())) {
            return false;
        }

        // Comprobar disponibilidad de sala y entrenador
        if (haySolapeSala(reserva.getSala().getIdSala(), inicio, fin)) {
            return false;
        }

        if (haySolapeEntrenador(reserva.getEntrenador().getIdEntrenador(), inicio, fin)) {
            return false;
        }

        // Comprobar disponibilidad de maquinaria
        if (!comprobarDisponibilidadMaquinaria(reserva)) {
            return false;
        }

        // Estado inicial de la reserva
        reserva.setEstado(Estado.Pendiente);

        // Asegurar la relación bidireccional con ReservaMaquinaria
        if (reserva.getMaquinariaAsignada() != null) {
            for (ReservaMaquinaria rm : reserva.getMaquinariaAsignada()) {
                rm.setReserva(reserva);
            }
        }

        reservaRepository.save(reserva);
        return true;
    }

    private boolean comprobarDisponibilidadMaquinaria(Reserva reserva) {
        if (reserva.getMaquinariaAsignada() == null || reserva.getMaquinariaAsignada().isEmpty()) {
            return true; // No hay maquinaria, no hay problema
        }

        LocalDateTime inicio = reserva.getFechaHoraInicio();
        LocalDateTime fin = reserva.getFechaHoraFin();
        TipoActividad tipo = reserva.getActividad().getTipoActividad();

        for (ReservaMaquinaria rm : reserva.getMaquinariaAsignada()) {
            if (rm.getMaquinaria() == null || rm.getCantidad() == null) {
                return false;
            }

            Long maquinariaId = rm.getMaquinaria().getIdMaquinaria();

            // Total ya reservado en el intervalo (para estados activos)
            int totalEnUso = totalMaquinariaReservadaEnIntervalo(
                    maquinariaId, inicio, fin, tipo
            );

            // Capacidad total de la maquinaria
            Optional<Maquinaria> maquinariaOpt = maquinariaRepository.findById(maquinariaId);
            if (maquinariaOpt.isEmpty()) {
                return false;
            }

            Maquinaria maquinaria = maquinariaOpt.get();

            // TODO: ajusta el nombre del campo de capacidad según tu entidad Maquinaria
            int capacidadTotal = maquinaria.getCantidadTotal(); // <-- cambia si tu campo se llama distinto

            if (totalEnUso + rm.getCantidad() > capacidadTotal) {
                return false; // no hay suficientes unidades disponibles
            }
        }

        return true;
    }

    @Override
    public boolean actualizarReserva(Long idReserva, Reserva datosActualizados) {
        if (idReserva == null || datosActualizados == null) {
            return false;
        }

        Optional<Reserva> opt = reservaRepository.findById(idReserva);
        if (opt.isEmpty()) {
            return false;
        }

        Reserva existente = opt.get();

        // Solo si no ha empezado
        if (!LocalDateTime.now().isBefore(existente.getFechaHoraInicio())) {
            return false;
        }

        // Actualizar SOLO si no son null
        if (datosActualizados.getFechaHoraInicio() != null) {
            existente.setFechaHoraInicio(datosActualizados.getFechaHoraInicio());
        }

        if (datosActualizados.getFechaHoraFin() != null) {
            existente.setFechaHoraFin(datosActualizados.getFechaHoraFin());
        }

        if (datosActualizados.getComentarios() != null) {
            existente.setComentarios(datosActualizados.getComentarios());
        }

        if (datosActualizados.getSala() != null) {
            existente.setSala(datosActualizados.getSala());
        }

        if (datosActualizados.getActividad() != null) {
            existente.setActividad(datosActualizados.getActividad());
        }

        if (datosActualizados.getEntrenador() != null) {
            existente.setEntrenador(datosActualizados.getEntrenador());
        }

        if (datosActualizados.getCliente() != null) {
            existente.setCliente(datosActualizados.getCliente());
        }

        // --- Actualización de maquinaria SOLO si viene en la petición ---
        if (datosActualizados.getMaquinariaAsignada() != null) {
            existente.getMaquinariaAsignada().clear();
            for (ReservaMaquinaria rm : datosActualizados.getMaquinariaAsignada()) {
                rm.setReserva(existente);
                existente.getMaquinariaAsignada().add(rm);
            }
        }

        reservaRepository.save(existente);
        return true;
    }

    @Override
    public boolean eliminarReserva(Long idReserva) {
        if (idReserva == null) return false;

        if (!reservaRepository.existsById(idReserva)) {
            return false;
        }

        // Uso interno/admin → no aplico restricciones de fecha/estado
        reservaRepository.deleteByIdReserva(idReserva);
        return true;
    }

    @Override
    public Reserva obtenerPorId(Long idReserva) {
        if (idReserva == null) return null;
        return reservaRepository.findById(idReserva).orElse(null);
    }

    @Override
    public boolean cancelarPorCliente(Long idReserva, Long idCliente) {
        if (idReserva == null || idCliente == null) return false;

        Optional<Reserva> opt = reservaRepository.findById(idReserva);
        if (opt.isEmpty()) return false;

        Reserva reserva = opt.get();

        // Comprobar que la reserva pertenece al cliente
        if (reserva.getCliente() == null ||
                !idCliente.equals(reserva.getCliente().getIdCliente())) {
            return false;
        }

        // Solo si no ha empezado
        if (!LocalDateTime.now().isBefore(reserva.getFechaHoraInicio())) {
            return false;
        }

        // No cancelar si ya está cancelada o completada
        if (reserva.getEstado() == Estado.Cancelada ||
                reserva.getEstado() == Estado.Completada) {
            return false;
        }

        reserva.setEstado(Estado.Cancelada);
        reservaRepository.save(reserva);
        return true;
    }

    @Override
    public boolean cancelarPorEntrenador(Long idReserva, Long idEntrenador) {
        if (idReserva == null || idEntrenador == null) return false;

        Optional<Reserva> opt = reservaRepository.findById(idReserva);
        if (opt.isEmpty()) return false;

        Reserva reserva = opt.get();

        // Comprobar que la reserva pertenece al entrenador
        if (reserva.getEntrenador() == null ||
                !idEntrenador.equals(reserva.getEntrenador().getIdEntrenador())) {
            return false;
        }

        // Solo si no ha empezado
        if (!LocalDateTime.now().isBefore(reserva.getFechaHoraInicio())) {
            return false;
        }

        if (reserva.getEstado() == Estado.Cancelada ||
                reserva.getEstado() == Estado.Completada) {
            return false;
        }

        reserva.setEstado(Estado.Cancelada);
        reservaRepository.save(reserva);
        return true;
    }

    @Override
    public boolean marcarComoCompletada(Long idReserva, Long idEntrenador) {
        if (idReserva == null || idEntrenador == null) return false;

        Optional<Reserva> opt = reservaRepository.findById(idReserva);
        if (opt.isEmpty()) return false;

        Reserva reserva = opt.get();

        // Comprobar que la acción la hace el entrenador de la reserva
        if (reserva.getEntrenador() == null ||
                !idEntrenador.equals(reserva.getEntrenador().getIdEntrenador())) {
            return false;
        }

        // Idealmente, solo se marca completada si ya ha finalizado
        if (LocalDateTime.now().isBefore(reserva.getFechaHoraFin())) {
            return false;
        }

        if (reserva.getEstado() != Estado.Pendiente) {
            return false;
        }

        reserva.setEstado(Estado.Completada);
        reservaRepository.save(reserva);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> listarHistorialCliente(Long idCliente) {
        if (idCliente == null) return List.of();
        return reservaRepository.findByClienteIdClienteOrderByFechaHoraInicioDesc(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> listarHistorialEntrenador(Long idEntrenador) {
        if (idEntrenador == null) return List.of();
        return reservaRepository.findByEntrenadorIdEntrenadorOrderByFechaHoraInicioDesc(idEntrenador);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> listarProximasCliente(Long idCliente) {
        if (idCliente == null) return List.of();
        return reservaRepository.findByClienteIdClienteAndFechaHoraInicioAfter(
                idCliente,
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> listarProximasEntrenador(Long idEntrenador) {
        if (idEntrenador == null) return List.of();
        return reservaRepository.findByEntrenadorIdEntrenadorAndFechaHoraInicioAfter(
                idEntrenador,
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva obtenerProximaReservaCliente(Long idCliente) {
        if (idCliente == null) return null;

        Pageable top1 = PageRequest.of(0, 1);
        List<Reserva> lista = reservaRepository.proximaReservaCliente(
                idCliente,
                LocalDateTime.now(),
                top1
        );

        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva obtenerProximaClaseEntrenador(Long idEntrenador) {
        if (idEntrenador == null) return null;

        Pageable top1 = PageRequest.of(0, 1);
        List<Reserva> lista = reservaRepository.proximaClaseEntrenador(
                idEntrenador,
                LocalDateTime.now(),
                top1
        );

        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean haySolapeSala(Long salaId, LocalDateTime inicio, LocalDateTime fin) {
        if (salaId == null || inicio == null || fin == null) {
            return false;
        }

        // Consideramos ocupadas solo las reservas PENDIENTES
        return reservaRepository.existeSolapeSala(
                salaId, inicio, fin, Estado.Pendiente
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean haySolapeEntrenador(Long entrenadorId, LocalDateTime inicio, LocalDateTime fin) {
        if (entrenadorId == null || inicio == null || fin == null) {
            return false;
        }

        return reservaRepository.existeSolapeEntrenador(
                entrenadorId, inicio, fin, Estado.Pendiente
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaMaquinaria> obtenerMaquinariaDeReserva(Long idReserva) {
        if (idReserva == null) return List.of();
        return reservaMaquinariaRepository.findByReservaIdReserva(idReserva);
    }

    @Override
    public boolean eliminarMaquinariaDeReserva(Long idReserva) {
        if (idReserva == null) return false;

        try {
            reservaMaquinariaRepository.deleteByReservaIdReserva(idReserva);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int totalMaquinariaReservadaEnIntervalo(Long maquinariaId, LocalDateTime inicio, LocalDateTime fin, TipoActividad tipoActividadestadosActivos) {
        if (maquinariaId == null || inicio == null || fin == null) {
            return 0;
        }

        // Sumamos los estados "activos" que bloquean maquinaria.
        int totalPendiente = reservaMaquinariaRepository.totalReservadoEnIntervalo(
                maquinariaId, inicio, fin, Estado.Pendiente
        );

        int totalCompletada = reservaMaquinariaRepository.totalReservadoEnIntervalo(
                maquinariaId, inicio, fin, Estado.Completada
        );

        return totalPendiente + totalCompletada;
    }
}
