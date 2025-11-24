package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.model.Cliente;
import es.unex.mdai.FitReserve.data.model.Entrenador;
import es.unex.mdai.FitReserve.data.model.Reserva;
import es.unex.mdai.FitReserve.data.repository.ClienteRepository;
import es.unex.mdai.FitReserve.data.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteServicioImpl implements ClienteServicio{

    private final ClienteRepository clienteRepository;
    private final ReservaRepository reservaRepository;

    public ClienteServicioImpl(ClienteRepository clienteRepository, ReservaRepository reservaRepository) {
        this.clienteRepository = clienteRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    public boolean registrarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }
        if (cliente.getUsuario() == null) {
            throw new IllegalArgumentException("El cliente debe tener un usuario asociado.");
        }

        // Por @MapsId, idCliente = idUsuario
        Long idCliente = cliente.getUsuario().getIdUsuario();
        if (idCliente == null) {
            throw new IllegalArgumentException("El usuario asociado debe tener un idUsuario.");
        }

        boolean existe = clienteRepository.existsById(idCliente);
        if (existe) {
            // ya existe un cliente para ese usuario
            return false;
        }

        clienteRepository.save(cliente);
        return true;
    }

    @Override
    public boolean actualizarCliente(Long idCliente, Cliente datosActualizados) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El idCliente no puede ser nulo.");
        }
        if (datosActualizados == null) {
            throw new IllegalArgumentException("Los datos actualizados no pueden ser nulos.");
        }

        Cliente existente = clienteRepository.findByIdCliente(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("No existe cliente con id: " + idCliente));

        // Actualizamos sólo los campos propios del cliente
        if (datosActualizados.getFechaNacimiento() != null) {
            existente.setFechaNacimiento(datosActualizados.getFechaNacimiento());
        }
        if (datosActualizados.getGenero() != null) {
            existente.setGenero(datosActualizados.getGenero());
        }
        if (datosActualizados.getObjetivos() != null) {
            existente.setObjetivos(datosActualizados.getObjetivos());
        }

        clienteRepository.save(existente);
        return true;

    }

    @Override
    public boolean eliminarCliente(Long idCliente) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El idCliente no puede ser nulo.");
        }

        boolean existe = clienteRepository.existsById(idCliente);
        if (!existe) {
            return false;
        }

        clienteRepository.deleteById(idCliente);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerPorIdUsuario(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("El idUsuario no puede ser nulo.");
        }

        return clienteRepository.findByUsuarioIdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe entrenador asociado al usuario con id: " + idUsuario));
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerClientePorId(Long idCliente) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El idCliente no puede ser nulo.");
        }

        return clienteRepository.findByIdCliente(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("No existe cliente con id: " + idCliente));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> obtenerHistorialReservas(Long idCliente) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El idCliente no puede ser nulo.");
        }

        Cliente cliente = clienteRepository.findByIdCliente(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("No existe cliente con id: " + idCliente));

        return cliente.getReservas();
    }
}
