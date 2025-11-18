package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Maquinaria;

import java.time.LocalDateTime;
import java.util.List;

public interface MaquinariaServicio {

    // --- CRUD básico ---

    /**
     * Crea una nueva maquinaria.
     */
    Maquinaria crearMaquinaria(Maquinaria maquinaria);

    /**
     * Actualiza una maquinaria existente.
     */
    Maquinaria actualizarMaquinaria(Long idMaquinaria, Maquinaria maquinariaActualizada);

    /**
     * Elimina una maquinaria por su ID.
     * Según tu regla de negocio, al eliminar una maquinaria
     * también deberían gestionarse las reservas asociadas.
     */
    void eliminarMaquinaria(Long idMaquinaria);

    /**
     * Obtiene una maquinaria por su ID.
     */
    Maquinaria obtenerMaquinariaPorId(Long idMaquinaria);

    /**
     * Obtiene una maquinaria por su nombre.
     */
    Maquinaria obtenerPorNombre(String nombre);

    /**
     * Lista toda la maquinaria registrada.
     */
    List<Maquinaria> listarTodas();


    // --- Consultas específicas / dominio ---

    /**
     * Lista maquinaria filtrando por tipo de actividad (cardio, fuerza, etc.).
     */
    List<Maquinaria> buscarPorTipoActividad(TipoActividad tipoActividad);

    /**
     * Variante para buscar maquinaria disponible de un tipo concreto.
     */
    List<Maquinaria> buscarDisponibles(LocalDateTime inicio,
                                       LocalDateTime fin,
                                       TipoActividad tipoActividad);
}
