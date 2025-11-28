package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.Sala;
import es.unex.mdai.FitReserve.data.repository.SalaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalaServicioImpl implements SalaServicio {

    private final SalaRepository salaRepository;

    public SalaServicioImpl(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    @Override
    @Transactional
    public boolean crearSala(Sala sala) {
        if (sala == null) {
            return false;
        }

        // Validaciones básicas
        if (sala.getNombre() == null || sala.getNombre().isBlank()) {
            return false;
        }
        if (sala.getUbicacion() == null || sala.getUbicacion().isBlank()) {
            return false;
        }
        if (sala.getCapacidad() <= 0) {
            return false;
        }

        // No permitir nombres duplicados
        if (salaRepository.findByNombre(sala.getNombre()).isPresent()) {
            return false;
        }

        salaRepository.save(sala);
        return true;
    }

    @Override
    @Transactional
    public boolean actualizarSala(Long idSala, Sala salaActualizada) {
        if (idSala == null) {
            throw new IllegalArgumentException("El idSala no puede ser nulo.");
        }
        if (salaActualizada == null) {
            throw new IllegalArgumentException("Los datos actualizados no pueden ser nulos.");
        }

        var opt = salaRepository.findByIdSala(idSala);
        if (opt.isEmpty()) {
            return false; // igual que haces en otros servicios cuando no existe
        }

        Sala existente = opt.get();

        // ACTUALIZAR solo campos no nulos / válidos
        if (salaActualizada.getNombre() != null) {
            existente.setNombre(salaActualizada.getNombre());
        }
        if (salaActualizada.getUbicacion() != null) {
            existente.setUbicacion(salaActualizada.getUbicacion());
        }
        if (salaActualizada.getDescripcion() != null) {
            existente.setDescripcion(salaActualizada.getDescripcion());
        }
        if (salaActualizada.getCapacidad() > 0) {
            existente.setCapacidad(salaActualizada.getCapacidad());
        }

        salaRepository.save(existente);
        return true;
    }

    @Override
    @Transactional
    public boolean eliminarSala(Long idSala) {
        if (idSala == null) {
            throw new IllegalArgumentException("El idSala no puede ser nulo.");
        }

        // IMPORTANTE: esta llamada se ejecuta ahora dentro de una transacción
        if (!salaRepository.existsById(idSala)) {
            return false;
        }

        // Si tienes un deleteByIdSala derivado, se usará aquí
        salaRepository.deleteByIdSala(idSala);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Sala obtenerSalaPorId(Long idSala) {
        if (idSala == null) {
            throw new IllegalArgumentException("El idSala no puede ser nulo.");
        }

        // Cambiamos para que devuelva null si no existe, igual que
        // tu controlador de usuarios espera (if (cliente == null) ...)
        return salaRepository.findByIdSala(idSala).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sala> listarTodas() {
        return salaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sala> buscarSalasDisponibles(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Inicio y fin no pueden ser nulos.");
        }

        return salaRepository.findDisponibles(inicio, fin, Estado.Pendiente);
    }
}
