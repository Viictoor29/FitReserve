package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.Entrenador;
import es.unex.mdai.FitReserve.data.model.Reserva;
import es.unex.mdai.FitReserve.data.repository.EntrenadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EntrenadorServicioImpl implements  EntrenadorServicio {

    private final EntrenadorRepository entrenadorRepository;

    public EntrenadorServicioImpl(EntrenadorRepository entrenadorRepository) {
        this.entrenadorRepository = entrenadorRepository;
    }

    @Override
    public boolean registrarEntrenador(Entrenador entrenador) {
        if (entrenador == null) {
            throw new IllegalArgumentException("El entrenador no puede ser nulo.");
        }
        if (entrenador.getUsuario() == null || entrenador.getUsuario().getIdUsuario() == null) {
            throw new IllegalArgumentException("El entrenador debe tener un usuario con idUsuario.");
        }

        Long idEntrenador = entrenador.getUsuario().getIdUsuario(); // por @MapsId
        if (entrenadorRepository.existsById(idEntrenador)) {
            // ya existe un entrenador asociado a ese usuario
            return false;
        }

        entrenadorRepository.save(entrenador);
        return true;
    }

    @Override
    public boolean actualizarEntrenador(Long idEntrenador, Entrenador datosActualizados) {
        if (idEntrenador == null) {
            throw new IllegalArgumentException("El idEntrenador no puede ser nulo.");
        }
        if (datosActualizados == null) {
            throw new IllegalArgumentException("Los datos actualizados no pueden ser nulos.");
        }

        Entrenador existente = entrenadorRepository.findByIdEntrenador(idEntrenador)
                .orElseThrow(() -> new IllegalArgumentException("No existe entrenador con id: " + idEntrenador));

        if (datosActualizados.getEspecialidad() != null) {
            existente.setEspecialidad(datosActualizados.getEspecialidad());
        }
        if (datosActualizados.getExperiencia() != 0) {
            existente.setExperiencia(datosActualizados.getExperiencia());
        }
        if (datosActualizados.getHoraInicioTrabajo() != null) {
            existente.setHoraInicioTrabajo(datosActualizados.getHoraInicioTrabajo());
        }
        if (datosActualizados.getHoraFinTrabajo() != null) {
            existente.setHoraFinTrabajo(datosActualizados.getHoraFinTrabajo());
        }

        entrenadorRepository.save(existente);
        return true;
    }

    @Override
    public boolean eliminarEntrenador(Long idEntrenador) {
        if (idEntrenador == null) {
            throw new IllegalArgumentException("El idEntrenador no puede ser nulo.");
        }

        boolean existe = entrenadorRepository.existsById(idEntrenador);
        if (!existe) {
            return false;
        }

        entrenadorRepository.deleteById(idEntrenador);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Entrenador obtenerEntrenadorPorId(Long idEntrenador) {
        if (idEntrenador == null) {
            throw new IllegalArgumentException("El idEntrenador no puede ser nulo.");
        }

        return entrenadorRepository.findByIdEntrenador(idEntrenador)
                .orElseThrow(() -> new IllegalArgumentException("No existe entrenador con id: " + idEntrenador));
    }

    @Override
    @Transactional(readOnly = true)
    public Entrenador obtenerPorIdUsuario(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("El idUsuario no puede ser nulo.");
        }

        return entrenadorRepository.findByUsuarioIdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe entrenador asociado al usuario con id: " + idUsuario));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Entrenador> listarTodos() {
        return entrenadorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> obtenerClases(Long idEntrenador) {
        if (idEntrenador == null) {
            throw new IllegalArgumentException("El idEntrenador no puede ser nulo.");
        }

        Entrenador entrenador = entrenadorRepository.findByIdEntrenador(idEntrenador)
                .orElseThrow(() -> new IllegalArgumentException("No existe entrenador con id: " + idEntrenador));

        return entrenador.getReservas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Entrenador> buscarDisponibles(LocalDateTime inicio, LocalDateTime fin, String especialidad) {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas.");
        }
        if (!fin.isAfter(inicio)) {
            throw new IllegalArgumentException("La fecha fin debe ser posterior a la de inicio.");
        }

        var estadosOcupados = Estado.Pendiente;

        return entrenadorRepository.findDisponibles(inicio, fin, estadosOcupados, especialidad);
    }
}
