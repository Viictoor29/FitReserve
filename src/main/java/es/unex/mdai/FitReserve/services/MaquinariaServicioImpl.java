package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Maquinaria;
import es.unex.mdai.FitReserve.data.repository.MaquinariaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public class MaquinariaServicioImpl implements  MaquinariaServicio {

    private final MaquinariaRepository maquinariaRepository;

    public MaquinariaServicioImpl(MaquinariaRepository maquinariaRepository) {
        this.maquinariaRepository = maquinariaRepository;
    }

    @Override
    public boolean crearMaquinaria(Maquinaria maquinaria) {
        if (maquinaria == null) {
            throw new IllegalArgumentException("La maquinaria no puede ser nula.");
        }

        // No permitir nombres duplicados
        if (maquinariaRepository.findByNombre(maquinaria.getNombre()).isPresent()) {
            return false;
        }

        maquinariaRepository.save(maquinaria);
        return true;
    }

    @Override
    public boolean actualizarMaquinaria(Long idMaquinaria, Maquinaria maquinariaActualizada) {
        if (idMaquinaria == null) {
            throw new IllegalArgumentException("El idMaquinaria no puede ser nulo.");
        }
        if (maquinariaActualizada == null) {
            throw new IllegalArgumentException("Los datos actualizados no pueden ser nulos.");
        }

        Maquinaria existente = maquinariaRepository.findByIdMaquinaria(idMaquinaria)
                .orElseThrow(() -> new IllegalArgumentException("No existe maquinaria con id: " + idMaquinaria));

        // Actualizaciones parciales
        if (maquinariaActualizada.getNombre() != null) {
            existente.setNombre(maquinariaActualizada.getNombre());
        }

        if (maquinariaActualizada.getCantidadTotal() > 0) {
            existente.setCantidadTotal(maquinariaActualizada.getCantidadTotal());
        }

        if (maquinariaActualizada.getTipoActividad() != null) {
            existente.setTipoActividad(maquinariaActualizada.getTipoActividad());
        }

        if (maquinariaActualizada.getDescripcion() != null) {
            existente.setDescripcion(maquinariaActualizada.getDescripcion());
        }

        maquinariaRepository.save(existente);
        return true;
    }

    @Override
    public boolean eliminarMaquinaria(Long idMaquinaria) {
        if (idMaquinaria == null) {
            throw new IllegalArgumentException("El idMaquinaria no puede ser nulo.");
        }

        if (!maquinariaRepository.existsById(idMaquinaria)) {
            return false;
        }

        maquinariaRepository.deleteById(idMaquinaria);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Maquinaria obtenerMaquinariaPorId(Long idMaquinaria) {
        if (idMaquinaria == null) {
            throw new IllegalArgumentException("El idMaquinaria no puede ser nulo.");
        }

        return maquinariaRepository.findByIdMaquinaria(idMaquinaria)
                .orElseThrow(() -> new IllegalArgumentException("No existe maquinaria con id: " + idMaquinaria));
    }

    @Override
    @Transactional(readOnly = true)
    public Maquinaria obtenerPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío.");
        }

        return maquinariaRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("No existe maquinaria con nombre: " + nombre));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Maquinaria> listarTodas() {
        return maquinariaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Maquinaria> buscarPorTipoActividad(TipoActividad tipoActividad) {
        if (tipoActividad == null) {
            throw new IllegalArgumentException("El tipo de actividad no puede ser nulo.");
        }

        return maquinariaRepository.findByTipoActividad(tipoActividad);
    }

    @Override
    public List<Maquinaria> buscarDisponibles(LocalDateTime inicio, LocalDateTime fin, TipoActividad tipoActividad) {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("El intervalo no puede ser nulo.");
        }

        // 1. Buscar todas las disponibles según reservas pendientes
        List<Maquinaria> disponibles = maquinariaRepository.findDisponibles(inicio, fin, Estado.Pendiente);

        // 2. Si piden filtrar por tipoActividad:
        if (tipoActividad != null) {
            return disponibles.stream()
                    .filter(m -> m.getTipoActividad() == tipoActividad)
                    .toList();
        }

        return disponibles;
    }
}
