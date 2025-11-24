package es.unex.mdai.FitReserve.testServices;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.Sala;
import es.unex.mdai.FitReserve.data.repository.SalaRepository;
import es.unex.mdai.FitReserve.services.SalaServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestSalaServicioImpl {

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private SalaServicioImpl salaServicio;

    private Sala sala;

    @BeforeEach
    void setUp() {
        sala = new Sala();
        sala.setIdSala(1L);
        sala.setNombre("Sala A");
        sala.setUbicacion("Planta 1");
        sala.setDescripcion("Sala de yoga");
        sala.setCapacidad(20);
    }

    // ---------------------------------------------------------
    // crearSala
    // ---------------------------------------------------------

    @Test
    void crearSala_deberiaCrearSiNombreNoDuplicado() {
        when(salaRepository.findByNombre("Sala A")).thenReturn(Optional.empty());

        boolean resultado = salaServicio.crearSala(sala);

        assertThat(resultado).isTrue();
        verify(salaRepository).save(sala);
    }

    @Test
    void crearSala_deberiaRetornarFalseSiNombreDuplicado() {
        when(salaRepository.findByNombre("Sala A")).thenReturn(Optional.of(sala));

        boolean resultado = salaServicio.crearSala(sala);

        assertThat(resultado).isFalse();
        verify(salaRepository, never()).save(any());
    }

    @Test
    void crearSala_lanzaExcepcionSiSalaEsNula() {
        assertThatThrownBy(() -> salaServicio.crearSala(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La sala no puede ser nula.");
    }

    // ---------------------------------------------------------
    // actualizarSala
    // ---------------------------------------------------------

    @Test
    void actualizarSala_deberiaActualizarCamposNoNulos() {
        Sala actualizada = new Sala();
        actualizada.setNombre("Sala B");
        actualizada.setDescripcion("Nueva descripción");
        actualizada.setUbicacion("Planta 2");
        actualizada.setCapacidad(40);

        when(salaRepository.findByIdSala(1L))
                .thenReturn(Optional.of(sala));

        boolean resultado = salaServicio.actualizarSala(1L, actualizada);

        assertThat(resultado).isTrue();
        assertThat(sala.getNombre()).isEqualTo("Sala B");
        assertThat(sala.getDescripcion()).isEqualTo("Nueva descripción");
        assertThat(sala.getUbicacion()).isEqualTo("Planta 2");
        assertThat(sala.getCapacidad()).isEqualTo(40);
        verify(salaRepository).save(sala);
    }

    @Test
    void actualizarSala_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> salaServicio.actualizarSala(null, sala))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idSala no puede ser nulo.");
    }

    @Test
    void actualizarSala_lanzaExcepcionSiActualizadaEsNula() {
        assertThatThrownBy(() -> salaServicio.actualizarSala(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Los datos actualizados no pueden ser nulos.");
    }

    @Test
    void actualizarSala_lanzaExcepcionSiNoExiste() {
        when(salaRepository.findByIdSala(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salaServicio.actualizarSala(1L, sala))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe sala con id: 1");
    }

    // ---------------------------------------------------------
    // eliminarSala
    // ---------------------------------------------------------

    @Test
    void eliminarSala_deberiaEliminarSiExiste() {
        when(salaRepository.existsById(1L)).thenReturn(true);

        boolean resultado = salaServicio.eliminarSala(1L);

        assertThat(resultado).isTrue();
        verify(salaRepository).deleteByIdSala(1L);
    }

    @Test
    void eliminarSala_deberiaRetornarFalseSiNoExiste() {
        when(salaRepository.existsById(1L)).thenReturn(false);

        boolean resultado = salaServicio.eliminarSala(1L);

        assertThat(resultado).isFalse();
        verify(salaRepository, never()).deleteByIdSala(any());
    }

    @Test
    void eliminarSala_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> salaServicio.eliminarSala(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idSala no puede ser nulo.");
    }

    // ---------------------------------------------------------
    // obtenerSalaPorId
    // ---------------------------------------------------------

    @Test
    void obtenerSalaPorId_deberiaDevolverSala() {
        when(salaRepository.findByIdSala(1L)).thenReturn(Optional.of(sala));

        Sala resultado = salaServicio.obtenerSalaPorId(1L);

        assertThat(resultado).isEqualTo(sala);
    }

    @Test
    void obtenerSalaPorId_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> salaServicio.obtenerSalaPorId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idSala no puede ser nulo.");
    }

    @Test
    void obtenerSalaPorId_lanzaExcepcionSiNoExiste() {
        when(salaRepository.findByIdSala(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salaServicio.obtenerSalaPorId(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe sala con id: 1");
    }

    // ---------------------------------------------------------
    // listarTodas
    // ---------------------------------------------------------

    @Test
    void listarTodas_deberiaDevolverLista() {
        when(salaRepository.findAll()).thenReturn(List.of(sala));

        List<Sala> lista = salaServicio.listarTodas();

        assertThat(lista).containsExactly(sala);
    }

    // ---------------------------------------------------------
    // buscarSalasDisponibles
    // ---------------------------------------------------------

    @Test
    void buscarSalasDisponibles_deberiaDelegarEnRepositorio() {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusHours(1);

        when(salaRepository.findDisponibles(inicio, fin, Estado.Pendiente))
                .thenReturn(List.of(sala));

        List<Sala> resultado = salaServicio.buscarSalasDisponibles(inicio, fin);

        assertThat(resultado).containsExactly(sala);
    }

    @Test
    void buscarSalasDisponibles_lanzaExcepcionSiInicioONulo() {
        LocalDateTime ahora = LocalDateTime.now();

        assertThatThrownBy(() -> salaServicio.buscarSalasDisponibles(null, ahora))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Inicio y fin no pueden ser nulos.");

        assertThatThrownBy(() -> salaServicio.buscarSalasDisponibles(ahora, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Inicio y fin no pueden ser nulos.");
    }
}
