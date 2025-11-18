package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.Reserva;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaServicio {
    // --- CRUD / gestión principal ---

    /**
     * Crea una nueva reserva.
     * Debe verificar disponibilidad de sala, entrenador y maquinaria
     * según la lógica de negocio antes de guardar.
     */
    Reserva crearReserva(Reserva reserva);

    /**
     * Modifica una reserva existente (solo si no ha empezado).
     */
    Reserva actualizarReserva(Long idReserva, Reserva datosActualizados);

    /**
     * Elimina una reserva (uso interno / admin).
     */
    void eliminarReserva(Long idReserva);

    /**
     * Obtiene una reserva por su ID.
     */
    Reserva obtenerPorId(Long idReserva);


    // --- Cancelaciones y cambio de estado ---

    /**
     * Cancelación iniciada por el cliente.
     */
    void cancelarPorCliente(Long idReserva, Long idCliente);

    /**
     * Cancelación iniciada por el entrenador.
     */
    void cancelarPorEntrenador(Long idReserva, Long idEntrenador);

    /**
     * Marca la reserva como COMPLETADA.
     * Solo debería poder hacerlo el entrenador.
     */
    void marcarComoCompletada(Long idReserva, Long idEntrenador);


    // --- Listados para cliente y entrenador ---

    /**
     * Historial completo de reservas de un cliente (ordenado desc. por fecha).
     */
    List<Reserva> listarHistorialCliente(Long idCliente);

    /**
     * Historial completo de clases de un entrenador (ordenado desc. por fecha).
     */
    List<Reserva> listarHistorialEntrenador(Long idEntrenador);

    /**
     * Próximas reservas de un cliente (a partir de ahora).
     */
    List<Reserva> listarProximasCliente(Long idCliente);

    /**
     * Próximas clases de un entrenador (a partir de ahora).
     */
    List<Reserva> listarProximasEntrenador(Long idEntrenador);

    /**
     * Próxima reserva del cliente (la más cercana en el futuro), o null si no hay.
     */
    Reserva obtenerProximaReservaCliente(Long idCliente);

    /**
     * Próxima clase del entrenador (la más cercana en el futuro), o null si no hay.
     */
    Reserva obtenerProximaClaseEntrenador(Long idEntrenador);


    // --- Comprobación de disponibilidad (usa internamente los exists del repo) ---

    /**
     * Comprueba si hay solape de reservas en una sala
     * dentro del intervalo indicado.
     */
    boolean haySolapeSala(Long salaId,
                          LocalDateTime inicio,
                          LocalDateTime fin);

    /**
     * Comprueba si hay solape de reservas para un entrenador
     * dentro del intervalo indicado.
     */
    boolean haySolapeEntrenador(Long entrenadorId,
                                LocalDateTime inicio,
                                LocalDateTime fin);


    /**
     * Permite cambiar el estado de una reserva de forma genérica
     * (uso interno/controlado).
     */
    Reserva cambiarEstado(Long idReserva, Estado nuevoEstado);
}
