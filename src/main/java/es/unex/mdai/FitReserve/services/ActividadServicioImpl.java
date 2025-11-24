package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.enume.NivelActividad;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Actividad;
import es.unex.mdai.FitReserve.data.repository.ActividadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActividadServicioImpl implements ActividadServicio {


    private final ActividadRepository actividadRepository;

    public ActividadServicioImpl(ActividadRepository actividadRepository) {
        this.actividadRepository = actividadRepository;
    }

    @Override
    public boolean crearActividad(Actividad actividad) {
        if (actividad == null) {
            throw new IllegalArgumentException("La actividad no puede ser nula.");
        }

        // No permitir nombres duplicados
        if (actividad.getNombre() != null &&
                actividadRepository.findByNombre(actividad.getNombre()).isPresent()) {
            return false; // ya existe
        }

        actividadRepository.save(actividad);
        return true;
    }

    @Override
    public boolean actualizarActividad(Long idActividad, Actividad actividadActualizada) {
        if (idActividad == null) {
            throw new IllegalArgumentException("El idActividad no puede ser nulo.");
        }
        if (actividadActualizada == null) {
            throw new IllegalArgumentException("Los datos actualizados no pueden ser nulos.");
        }

        Actividad existente = actividadRepository.findByIdActividad(idActividad)
                .orElseThrow(() -> new IllegalArgumentException("No existe actividad con id: " + idActividad));

        // actualizar solo los campos no nulos
        if (actividadActualizada.getNombre() != null) {
            existente.setNombre(actividadActualizada.getNombre());
        }
        if (actividadActualizada.getDescripcion() != null) {
            existente.setDescripcion(actividadActualizada.getDescripcion());
        }
        if (actividadActualizada.getTipoActividad() != null) {
            existente.setTipoActividad(actividadActualizada.getTipoActividad());
        }
        if (actividadActualizada.getNivel() != null) {
            existente.setNivel(actividadActualizada.getNivel());
        }

        actividadRepository.save(existente);
        return true;
    }

    @Override
    public boolean eliminarActividad(Long idActividad) {
        if (idActividad == null) {
            throw new IllegalArgumentException("El idActividad no puede ser nulo.");
        }

        boolean existe = actividadRepository.existsById(idActividad);
        if (!existe) return false;

        actividadRepository.deleteByIdActividad(idActividad);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Actividad obtenerActividadPorId(Long idActividad) {
        if (idActividad == null) {
            throw new IllegalArgumentException("El idActividad no puede ser nulo.");
        }

        return actividadRepository.findByIdActividad(idActividad)
                .orElseThrow(() -> new IllegalArgumentException("No existe actividad con id: " + idActividad));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Actividad> listarTodas() {
        return actividadRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Actividad> buscarPorTipo(TipoActividad tipoActividad) {
        if (tipoActividad == null) {
            throw new IllegalArgumentException("El tipo de actividad no puede ser nulo.");
        }
        return actividadRepository.findByTipoActividad(tipoActividad);
    }

    @Override
    public List<Actividad> buscarPorNivel(NivelActividad nivel) {
        if (nivel == null) {
            throw new IllegalArgumentException("El nivel no puede ser nulo.");
        }
        return actividadRepository.findByNivel(nivel);
    }

    @Override
    public Actividad buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío.");
        }

        return actividadRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("No existe actividad con nombre: " + nombre));
    }
}
