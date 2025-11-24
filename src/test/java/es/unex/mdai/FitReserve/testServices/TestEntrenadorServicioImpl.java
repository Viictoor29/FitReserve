package es.unex.mdai.FitReserve.testServices;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.Entrenador;
import es.unex.mdai.FitReserve.data.model.Reserva;
import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.data.repository.EntrenadorRepository;
import es.unex.mdai.FitReserve.services.EntrenadorServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TestEntrenadorServicioImpl {

    @MockitoBean
    private EntrenadorRepository entrenadorRepository;

    @Autowired
    private EntrenadorServicioImpl entrenadorServicio;

    private Entrenador entrenador;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);

        entrenador = new Entrenador();
        entrenador.setIdEntrenador(1L);
        entrenador.setUsuario(usuario);
        entrenador.setEspecialidad("Cardio");
        entrenador.setExperiencia(5);
        entrenador.setHoraInicioTrabajo(LocalTime.of(8, 0));
        entrenador.setHoraFinTrabajo(LocalTime.of(16, 0));
    }

    // ---------------------------------------------------------
    // registrarEntrenador
    // ---------------------------------------------------------

    @Test
    void registrarEntrenador_deberiaRegistrarCuandoNoExiste() {
        when(entrenadorRepository.existsById(1L)).thenReturn(false);

        boolean resultado = entrenadorServicio.registrarEntrenador(entrenador);

        assertThat(resultado).isTrue();
        verify(entrenadorRepository).save(entrenador);
    }

    @Test
    void registrarEntrenador_deberiaRetornarFalseSiYaExiste() {
        when(entrenadorRepository.existsById(1L)).thenReturn(true);

        boolean resultado = entrenadorServicio.registrarEntrenador(entrenador);

        assertThat(resultado).isFalse();
        verify(entrenadorRepository, never()).save(any());
    }

    @Test
    void registrarEntrenador_lanzaExcepcionSiEntrenadorEsNulo() {
        assertThatThrownBy(() -> entrenadorServicio.registrarEntrenador(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El entrenador no puede ser nulo.");
    }

    @Test
    void registrarEntrenador_lanzaExcepcionSiUsuarioEsNulo() {
        Entrenador e = new Entrenador();

        assertThatThrownBy(() -> entrenadorServicio.registrarEntrenador(e))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El entrenador debe tener un usuario con idUsuario.");
    }

    @Test
    void registrarEntrenador_lanzaExcepcionSiIdUsuarioEsNulo() {
        Usuario u = new Usuario(); // idUsuario null
        Entrenador e = new Entrenador();
        e.setUsuario(u);

        assertThatThrownBy(() -> entrenadorServicio.registrarEntrenador(e))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El entrenador debe tener un usuario con idUsuario.");
    }

    // ---------------------------------------------------------
    // actualizarEntrenador
    // ---------------------------------------------------------

    @Test
    void actualizarEntrenador_deberiaActualizarCamposNoNulos() {
        Entrenador actualizados = new Entrenador();
        actualizados.setEspecialidad("Fuerza");
        actualizados.setExperiencia(10);
        actualizados.setHoraInicioTrabajo(LocalTime.of(9, 0));
        actualizados.setHoraFinTrabajo(LocalTime.of(18, 0));

        when(entrenadorRepository.findByIdEntrenador(1L))
                .thenReturn(Optional.of(entrenador));

        boolean resultado = entrenadorServicio.actualizarEntrenador(1L, actualizados);

        assertThat(resultado).isTrue();
        assertThat(entrenador.getEspecialidad()).isEqualTo("Fuerza");
        assertThat(entrenador.getExperiencia()).isEqualTo(10);
        assertThat(entrenador.getHoraInicioTrabajo()).isEqualTo(LocalTime.of(9, 0));
        assertThat(entrenador.getHoraFinTrabajo()).isEqualTo(LocalTime.of(18, 0));
        verify(entrenadorRepository).save(entrenador);
    }

    @Test
    void actualizarEntrenador_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> entrenadorServicio.actualizarEntrenador(null, entrenador))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idEntrenador no puede ser nulo.");
    }

    @Test
    void actualizarEntrenador_lanzaExcepcionSiDatosActualizadosEsNulo() {
        assertThatThrownBy(() -> entrenadorServicio.actualizarEntrenador(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Los datos actualizados no pueden ser nulos.");
    }

    @Test
    void actualizarEntrenador_lanzaExcepcionSiNoExiste() {
        when(entrenadorRepository.findByIdEntrenador(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> entrenadorServicio.actualizarEntrenador(1L, new Entrenador()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe entrenador con id: 1");
    }

    // ---------------------------------------------------------
    // eliminarEntrenador
    // ---------------------------------------------------------

    @Test
    void eliminarEntrenador_deberiaEliminarSiExiste() {
        when(entrenadorRepository.existsById(1L)).thenReturn(true);

        boolean resultado = entrenadorServicio.eliminarEntrenador(1L);

        assertThat(resultado).isTrue();
        verify(entrenadorRepository).deleteById(1L);
    }

    @Test
    void eliminarEntrenador_deberiaRetornarFalseSiNoExiste() {
        when(entrenadorRepository.existsById(1L)).thenReturn(false);

        boolean resultado = entrenadorServicio.eliminarEntrenador(1L);

        assertThat(resultado).isFalse();
        verify(entrenadorRepository, never()).deleteById(any());
    }

    @Test
    void eliminarEntrenador_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> entrenadorServicio.eliminarEntrenador(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idEntrenador no puede ser nulo.");
    }

    // ---------------------------------------------------------
    // obtenerEntrenadorPorId
    // ---------------------------------------------------------

    @Test
    void obtenerEntrenadorPorId_deberiaDevolverEntrenador() {
        when(entrenadorRepository.findByIdEntrenador(1L))
                .thenReturn(Optional.of(entrenador));

        Entrenador resultado = entrenadorServicio.obtenerEntrenadorPorId(1L);

        assertThat(resultado).isEqualTo(entrenador);
    }

    @Test
    void obtenerEntrenadorPorId_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> entrenadorServicio.obtenerEntrenadorPorId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idEntrenador no puede ser nulo.");
    }

    @Test
    void obtenerEntrenadorPorId_lanzaExcepcionSiNoExiste() {
        when(entrenadorRepository.findByIdEntrenador(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> entrenadorServicio.obtenerEntrenadorPorId(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe entrenador con id: 1");
    }

    // ---------------------------------------------------------
    // obtenerPorIdUsuario
    // ---------------------------------------------------------

    @Test
    void obtenerPorIdUsuario_deberiaDevolverEntrenador() {
        when(entrenadorRepository.findByUsuarioIdUsuario(1L))
                .thenReturn(Optional.of(entrenador));

        Entrenador resultado = entrenadorServicio.obtenerPorIdUsuario(1L);

        assertThat(resultado).isEqualTo(entrenador);
    }

    @Test
    void obtenerPorIdUsuario_lanzaExcepcionSiIdUsuarioEsNulo() {
        assertThatThrownBy(() -> entrenadorServicio.obtenerPorIdUsuario(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idUsuario no puede ser nulo.");
    }

    @Test
    void obtenerPorIdUsuario_lanzaExcepcionSiNoExiste() {
        when(entrenadorRepository.findByUsuarioIdUsuario(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> entrenadorServicio.obtenerPorIdUsuario(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe entrenador asociado al usuario con id: 1");
    }

    // ---------------------------------------------------------
    // listarTodos
    // ---------------------------------------------------------

    @Test
    void listarTodos_deberiaDevolverListaDeEntrenadores() {
        when(entrenadorRepository.findAll()).thenReturn(List.of(entrenador));

        List<Entrenador> lista = entrenadorServicio.listarTodos();

        assertThat(lista).containsExactly(entrenador);
    }

    // ---------------------------------------------------------
    // obtenerClases
    // ---------------------------------------------------------

    @Test
    void obtenerClases_deberiaDevolverReservasDelEntrenador() {
        Reserva r1 = new Reserva();
        Reserva r2 = new Reserva();
        entrenador.setReservas(List.of(r1, r2));

        when(entrenadorRepository.findByIdEntrenador(1L))
                .thenReturn(Optional.of(entrenador));

        List<Reserva> clases = entrenadorServicio.obtenerClases(1L);

        assertThat(clases).containsExactly(r1, r2);
    }

    @Test
    void obtenerClases_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> entrenadorServicio.obtenerClases(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idEntrenador no puede ser nulo.");
    }

    @Test
    void obtenerClases_lanzaExcepcionSiEntrenadorNoExiste() {
        when(entrenadorRepository.findByIdEntrenador(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> entrenadorServicio.obtenerClases(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe entrenador con id: 1");
    }

    // ---------------------------------------------------------
    // buscarDisponibles
    // ---------------------------------------------------------

    @Test
    void buscarDisponibles_deberiaLlamarAlRepositorioYDevolverLista() {
        LocalDateTime inicio = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime fin = inicio.plusHours(1);
        String especialidad = "Cardio";

        when(entrenadorRepository.findDisponibles(inicio, fin, Estado.Pendiente, especialidad))
                .thenReturn(List.of(entrenador));

        List<Entrenador> disponibles =
                entrenadorServicio.buscarDisponibles(inicio, fin, especialidad);

        assertThat(disponibles).containsExactly(entrenador);
        verify(entrenadorRepository).findDisponibles(
                eq(inicio), eq(fin), eq(Estado.Pendiente), eq(especialidad)
        );
    }

    @Test
    void buscarDisponibles_lanzaExcepcionSiInicioONulo() {
        LocalDateTime fin = LocalDateTime.now().plusHours(1);

        assertThatThrownBy(() -> entrenadorServicio.buscarDisponibles(null, fin, "Cardio"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Las fechas de inicio y fin no pueden ser nulas.");

        assertThatThrownBy(() -> entrenadorServicio.buscarDisponibles(fin.minusHours(1), null, "Cardio"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Las fechas de inicio y fin no pueden ser nulas.");
    }

    @Test
    void buscarDisponibles_lanzaExcepcionSiFinNoEsPosteriorAInicio() {
        LocalDateTime inicio = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime finIgual = inicio;
        LocalDateTime finAntes = inicio.minusMinutes(30);

        assertThatThrownBy(() -> entrenadorServicio.buscarDisponibles(inicio, finIgual, "Cardio"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La fecha fin debe ser posterior a la de inicio.");

        assertThatThrownBy(() -> entrenadorServicio.buscarDisponibles(inicio, finAntes, "Cardio"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La fecha fin debe ser posterior a la de inicio.");
    }
}
