package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.model.Cliente;
import es.unex.mdai.FitReserve.data.model.Reserva;
import es.unex.mdai.FitReserve.data.model.Usuario;

import java.util.List;

public interface ClienteServicio {
    // --- CRUD / gestión de cliente ---

    /**
     * Registra un nuevo cliente. Se asume que el Cliente ya tiene asociado su Usuario.
     */
    Cliente registrarCliente(Cliente cliente);

    /**
     * Actualiza los datos del cliente (datos personales, objetivos, etc.).
     */
    Cliente actualizarCliente(Long idCliente, Cliente datosActualizados);

    /**
     * Elimina el cliente (y, por cascada, lo que corresponda).
     */
    void eliminarCliente(Long idCliente);

    /**
     * Obtiene un cliente por su idCliente.
     */
    Cliente obtenerClientePorId(Long idCliente);

    /**
     * Obtiene un cliente a partir del id de Usuario (relación 1:1).
     */
    Cliente obtenerPorIdUsuario(Long idUsuario);

    /**
     * Lista todos los clientes (útil para el administrador).
     */
    List<Cliente> listarTodos();

    // --- Funcionalidades de dominio ---

    /**
     * Devuelve el historial de reservas del cliente.
     */
    List<Reserva> obtenerHistorialReservas(Long idCliente);
}
