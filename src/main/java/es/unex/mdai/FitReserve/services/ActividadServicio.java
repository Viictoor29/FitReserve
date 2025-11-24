package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.enume.NivelActividad;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Actividad;

import java.util.List;

public interface ActividadServicio {
    // --- CRUD básico ---

    /**
     * Crea una nueva actividad.
     */
    boolean crearActividad(Actividad actividad);

    /**
     * Actualiza una actividad existente.
     */
    boolean actualizarActividad(Long idActividad, Actividad actividadActualizada);

    /**
     * Elimina una actividad por su ID.
     */
    boolean eliminarActividad(Long idActividad);

    /**
     * Obtiene una actividad por su ID.
     */
    Actividad obtenerActividadPorId(Long idActividad);

    /**
     * Lista todas las actividades disponibles.
     */
    List<Actividad> listarTodas();


    // --- Consultas específicas ---

    /**
     * Busca actividades por tipo (Yoga, Spinning, etc.).
     */
    List<Actividad> buscarPorTipo(TipoActividad tipoActividad);

    /**
     * Busca actividades por nivel (Básico, Medio, Avanzado).
     */
    List<Actividad> buscarPorNivel(NivelActividad nivel);

    /**
     * Busca una actividad por su nombre.
     */
    Actividad buscarPorNombre(String nombre);
}
