package es.unex.mdai.FitReserve.testServices;

import es.unex.mdai.FitReserve.data.enume.Genero;
import es.unex.mdai.FitReserve.data.model.Cliente;
import es.unex.mdai.FitReserve.data.model.Reserva;
import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.data.repository.ClienteRepository;
import es.unex.mdai.FitReserve.data.repository.ReservaRepository;
import es.unex.mdai.FitReserve.services.ClienteServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TestClienteServicioImpl {

    @MockitoBean
    private ClienteRepository clienteRepository;

    @MockitoBean
    private ReservaRepository reservaRepository;

    @Autowired
    private ClienteServicioImpl clienteServicio;

    private Cliente cliente;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);

        cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setUsuario(usuario);
        cliente.setGenero(Genero.MASCULINO);
        cliente.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        cliente.setObjetivos("Bajar peso");
    }

    // ---------------------------------------------------------
    // registrarCliente
    // ---------------------------------------------------------

    @Test
    void registrarCliente_deberiaRegistrarCuandoNoExiste() {
        when(clienteRepository.existsById(1L)).thenReturn(false);

        boolean resultado = clienteServicio.registrarCliente(cliente);

        assertThat(resultado).isTrue();
        verify(clienteRepository).save(cliente);
    }

    @Test
    void registrarCliente_deberiaRetornarFalseSiYaExiste() {
        when(clienteRepository.existsById(1L)).thenReturn(true);

        boolean resultado = clienteServicio.registrarCliente(cliente);

        assertThat(resultado).isFalse();
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void registrarCliente_lanzaExcepcionSiClienteEsNulo() {
        assertThatThrownBy(() -> clienteServicio.registrarCliente(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El cliente no puede ser nulo.");
    }

    @Test
    void registrarCliente_lanzaExcepcionSiUsuarioEsNulo() {
        Cliente sinUsuario = new Cliente();

        assertThatThrownBy(() -> clienteServicio.registrarCliente(sinUsuario))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El cliente debe tener un usuario asociado.");
    }

    @Test
    void registrarCliente_lanzaExcepcionSiIdUsuarioEsNulo() {
        Usuario u = new Usuario();
        Cliente c = new Cliente();
        c.setUsuario(u);

        assertThatThrownBy(() -> clienteServicio.registrarCliente(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El usuario asociado debe tener un idUsuario.");
    }

    // ---------------------------------------------------------
    // actualizarCliente
    // ---------------------------------------------------------

    @Test
    void actualizarCliente_deberiaActualizarCamposNoNulos() {
        Cliente actualizados = new Cliente();
        actualizados.setFechaNacimiento(LocalDate.of(2000, 2, 2));
        actualizados.setGenero(Genero.FEMENINO);
        actualizados.setObjetivos("Ganar músculo");

        when(clienteRepository.findByIdCliente(1L))
                .thenReturn(Optional.of(cliente));

        boolean resultado = clienteServicio.actualizarCliente(1L, actualizados);

        assertThat(resultado).isTrue();
        assertThat(cliente.getFechaNacimiento()).isEqualTo(LocalDate.of(2000, 2, 2));
        assertThat(cliente.getGenero()).isEqualTo(Genero.FEMENINO);
        assertThat(cliente.getObjetivos()).isEqualTo("Ganar músculo");
        verify(clienteRepository).save(cliente);
    }

    @Test
    void actualizarCliente_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> clienteServicio.actualizarCliente(null, cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idCliente no puede ser nulo.");
    }

    @Test
    void actualizarCliente_lanzaExcepcionSiDatosActualizadosEsNulo() {
        assertThatThrownBy(() -> clienteServicio.actualizarCliente(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Los datos actualizados no pueden ser nulos.");
    }

    @Test
    void actualizarCliente_lanzaExcepcionSiClienteNoExiste() {
        when(clienteRepository.findByIdCliente(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteServicio.actualizarCliente(1L, new Cliente()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe cliente con id: 1");
    }

    // ---------------------------------------------------------
    // eliminarCliente
    // ---------------------------------------------------------

    @Test
    void eliminarCliente_deberiaEliminarSiExiste() {
        when(clienteRepository.existsById(1L)).thenReturn(true);

        boolean resultado = clienteServicio.eliminarCliente(1L);

        assertThat(resultado).isTrue();
        verify(clienteRepository).deleteById(1L);
    }

    @Test
    void eliminarCliente_deberiaRetornarFalseSiNoExiste() {
        when(clienteRepository.existsById(1L)).thenReturn(false);

        boolean resultado = clienteServicio.eliminarCliente(1L);

        assertThat(resultado).isFalse();
        verify(clienteRepository, never()).deleteById(any());
    }

    @Test
    void eliminarCliente_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> clienteServicio.eliminarCliente(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idCliente no puede ser nulo.");
    }

    // ---------------------------------------------------------
    // obtenerPorIdUsuario
    // ---------------------------------------------------------

    @Test
    void obtenerPorIdUsuario_deberiaDevolverCliente() {
        when(clienteRepository.findByUsuarioIdUsuario(1L))
                .thenReturn(Optional.of(cliente));

        Cliente resultado = clienteServicio.obtenerPorIdUsuario(1L);

        assertThat(resultado).isEqualTo(cliente);
    }

    @Test
    void obtenerPorIdUsuario_lanzaExcepcionSiIdUsuarioEsNulo() {
        assertThatThrownBy(() -> clienteServicio.obtenerPorIdUsuario(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idUsuario no puede ser nulo.");
    }

    @Test
    void obtenerPorIdUsuario_lanzaExcepcionSiNoExiste() {
        when(clienteRepository.findByUsuarioIdUsuario(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteServicio.obtenerPorIdUsuario(1L))
                .isInstanceOf(IllegalArgumentException.class)
                // Ojo: en el service pone "entrenador", respetamos el mensaje tal cual
                .hasMessage("No existe entrenador asociado al usuario con id: 1");
    }

    // ---------------------------------------------------------
    // obtenerClientePorId
    // ---------------------------------------------------------

    @Test
    void obtenerClientePorId_deberiaDevolverCliente() {
        when(clienteRepository.findByIdCliente(1L))
                .thenReturn(Optional.of(cliente));

        Cliente resultado = clienteServicio.obtenerClientePorId(1L);

        assertThat(resultado).isEqualTo(cliente);
    }

    @Test
    void obtenerClientePorId_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> clienteServicio.obtenerClientePorId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idCliente no puede ser nulo.");
    }

    @Test
    void obtenerClientePorId_lanzaExcepcionSiNoExiste() {
        when(clienteRepository.findByIdCliente(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteServicio.obtenerClientePorId(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe cliente con id: 1");
    }

    // ---------------------------------------------------------
    // listarTodos
    // ---------------------------------------------------------

    @Test
    void listarTodos_deberiaRetornarListaDeClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        List<Cliente> lista = clienteServicio.listarTodos();

        assertThat(lista).containsExactly(cliente);
    }

    // ---------------------------------------------------------
    // obtenerHistorialReservas
    // ---------------------------------------------------------

    @Test
    void obtenerHistorialReservas_deberiaDevolverReservasDelCliente() {
        Reserva r1 = new Reserva();
        Reserva r2 = new Reserva();
        cliente.setReservas(List.of(r1, r2));

        when(clienteRepository.findByIdCliente(1L))
                .thenReturn(Optional.of(cliente));

        List<Reserva> historial = clienteServicio.obtenerHistorialReservas(1L);

        assertThat(historial).containsExactly(r1, r2);
    }

    @Test
    void obtenerHistorialReservas_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> clienteServicio.obtenerHistorialReservas(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idCliente no puede ser nulo.");
    }

    @Test
    void obtenerHistorialReservas_lanzaExcepcionSiClienteNoExiste() {
        when(clienteRepository.findByIdCliente(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteServicio.obtenerHistorialReservas(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe cliente con id: 1");
    }
}
