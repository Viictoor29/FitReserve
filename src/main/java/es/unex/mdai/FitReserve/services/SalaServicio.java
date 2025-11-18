package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.model.Sala;

import java.time.LocalDateTime;
import java.util.List;

public interface SalaServicio {
    // --- CRUD básico ---
    Sala crearSala(Sala sala);

    Sala actualizarSala(Long idSala, Sala salaActualizada);

    void eliminarSala(Long idSala);

    Sala obtenerSalaPorId(Long idSala);

    List<Sala> listarTodas();

    // --- Disponibilidad ---
    /**
     * Devuelve las salas que NO están ocupadas por reservas solapadas
     * en el intervalo indicado.
     */
    List<Sala> buscarSalasDisponibles(LocalDateTime inicio, LocalDateTime fin);
}
