package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.model.Entrenador;
import es.unex.mdai.FitReserve.data.model.Reserva;
import es.unex.mdai.FitReserve.data.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

public interface EntrenadorServicio {
    // --- CRUD / gestión de entrenador ---

    /**
     * Registra un nuevo entrenador. Se asume que Entrenador ya tiene asociado su Usuario.
     */
    boolean registrarEntrenador(Entrenador entrenador);

    /**
     * Actualiza los datos del entrenador (especialidad, bio, etc.).
     */
    boolean actualizarEntrenador(Long idEntrenador, Entrenador datosActualizados);

    /**
     * Elimina el entrenador.
     */
    boolean eliminarEntrenador(Long idEntrenador);

    /**
     * Obtiene un entrenador por su idEntrenador.
     */
    Entrenador obtenerEntrenadorPorId(Long idEntrenador);

    /**
     * Obtiene un entrenador a partir del id de Usuario (relación 1:1).
     */
    Entrenador obtenerPorIdUsuario(Long idUsuario);

    /**
     * Lista todos los entrenadores (útil para el administrador).
     */
    List<Entrenador> listarTodos();

    // --- Funcionalidades de dominio ---

    /**
     * Clases/reservas que tiene que impartir el entrenador.
     */
    List<Reserva> obtenerClases(Long idEntrenador);

    /**
     * Busca entrenadores disponibles en un rango horario y, opcionalmente, con una especialidad.
     */
    List<Entrenador> buscarDisponibles(LocalDateTime inicio,
                                       LocalDateTime fin,
                                       String especialidad);


}
