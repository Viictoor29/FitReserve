package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.model.Cliente;
import es.unex.mdai.FitReserve.data.model.Reserva;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServicioImpl implements ClienteServicio{

    @Override
    public boolean registrarCliente(Cliente cliente) {
        return false;
    }

    @Override
    public Cliente actualizarCliente(Long idCliente, Cliente datosActualizados) {
        return null;
    }

    @Override
    public boolean eliminarCliente(Long idCliente) {

        return false;
    }

    @Override
    public Cliente obtenerClientePorId(Long idCliente) {
        return null;
    }

    @Override
    public List<Cliente> listarTodos() {
        return List.of();
    }

    @Override
    public List<Reserva> obtenerHistorialReservas(Long idCliente) {
        return List.of();
    }
}
