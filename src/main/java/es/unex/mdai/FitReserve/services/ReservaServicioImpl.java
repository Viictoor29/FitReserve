package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Reserva;
import es.unex.mdai.FitReserve.data.model.ReservaMaquinaria;
import es.unex.mdai.FitReserve.data.repository.EntrenadorRepository;
import es.unex.mdai.FitReserve.data.repository.MaquinariaRepository;
import es.unex.mdai.FitReserve.data.repository.ReservaMaquinariaRepository;
import es.unex.mdai.FitReserve.data.repository.ReservaRepository;

import java.time.LocalDateTime;
import java.util.List;

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
        return false;
    }

    @Override
    public boolean actualizarReserva(Long idReserva, Reserva datosActualizados) {
        return false;
    }

    @Override
    public boolean eliminarReserva(Long idReserva) {
        return false;
    }

    @Override
    public Reserva obtenerPorId(Long idReserva) {
        return null;
    }

    @Override
    public boolean cancelarPorCliente(Long idReserva, Long idCliente) {
        return false;
    }

    @Override
    public boolean cancelarPorEntrenador(Long idReserva, Long idEntrenador) {
        return false;
    }

    @Override
    public boolean marcarComoCompletada(Long idReserva, Long idEntrenador) {
        return false;
    }

    @Override
    public List<Reserva> listarHistorialCliente(Long idCliente) {
        return List.of();
    }

    @Override
    public List<Reserva> listarHistorialEntrenador(Long idEntrenador) {
        return List.of();
    }

    @Override
    public List<Reserva> listarProximasCliente(Long idCliente) {
        return List.of();
    }

    @Override
    public List<Reserva> listarProximasEntrenador(Long idEntrenador) {
        return List.of();
    }

    @Override
    public Reserva obtenerProximaReservaCliente(Long idCliente) {
        return null;
    }

    @Override
    public Reserva obtenerProximaClaseEntrenador(Long idEntrenador) {
        return null;
    }

    @Override
    public boolean haySolapeSala(Long salaId, LocalDateTime inicio, LocalDateTime fin) {
        return false;
    }

    @Override
    public boolean haySolapeEntrenador(Long entrenadorId, LocalDateTime inicio, LocalDateTime fin) {
        return false;
    }

    @Override
    public Reserva cambiarEstado(Long idReserva, Estado nuevoEstado) {
        return null;
    }

    @Override
    public List<ReservaMaquinaria> obtenerMaquinariaDeReserva(Long idReserva) {
        return List.of();
    }

    @Override
    public boolean eliminarMaquinariaDeReserva(Long idReserva) {
        return false;
    }

    @Override
    public int totalMaquinariaReservadaEnIntervalo(Long maquinariaId, LocalDateTime inicio, LocalDateTime fin, TipoActividad tipoActividadestadosActivos) {
        return 0;
    }
}
