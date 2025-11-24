package es.unex.mdai.FitReserve.testServices;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Maquinaria;
import es.unex.mdai.FitReserve.data.repository.MaquinariaRepository;
import es.unex.mdai.FitReserve.services.MaquinariaServicioImpl;
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
public class TestMaquinariaServicioImpl {

    @Mock
    private MaquinariaRepository maquinariaRepository;

    @InjectMocks
    private MaquinariaServicioImpl maquinariaServicio;

    private Maquinaria maquinaria;

    @BeforeEach
    void setUp() {
        maquinaria = new Maquinaria();
        maquinaria.setIdMaquinaria(1L);
        maquinaria.setNombre("Bicicleta");
        maquinaria.setCantidadTotal(10);
        maquinaria.setDescripcion("Bici para cardio");
        maquinaria.setTipoActividad(TipoActividad.CARDIO);
    }

    // ---------------------------------------------------------
    // crearMaquinaria
    // ---------------------------------------------------------

    @Test
    void crearMaquinaria_deberiaCrearCuandoNoHayNombreDuplicado() {
        when(maquinariaRepository.findByNombre("Bicicleta"))
                .thenReturn(Optional.empty());

        boolean resultado = maquinariaServicio.crearMaquinaria(maquinaria);

        assertThat(resultado).isTrue();
        verify(maquinariaRepository).save(maquinaria);
    }

    @Test
    void crearMaquinaria_deberiaRetornarFalseSiNombreDuplicado() {
        when(maquinariaRepository.findByNombre("Bicicleta"))
                .thenReturn(Optional.of(maquinaria));

        boolean resultado = maquinariaServicio.crearMaquinaria(maquinaria);

        assertThat(resultado).isFalse();
        verify(maquinariaRepository, never()).save(any());
    }

    @Test
    void crearMaquinaria_lanzaExcepcionSiEsNula() {
        assertThatThrownBy(() -> maquinariaServicio.crearMaquinaria(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La maquinaria no puede ser nula.");
    }

    // ---------------------------------------------------------
    // actualizarMaquinaria
    // ---------------------------------------------------------

    @Test
    void actualizarMaquinaria_deberiaActualizarCamposNoNulos() {
        Maquinaria actualizada = new Maquinaria();
        actualizada.setNombre("Bici Pro");
        actualizada.setCantidadTotal(20);
        actualizada.setTipoActividad(TipoActividad.FUERZA);
        actualizada.setDescripcion("Nueva descripción");

        when(maquinariaRepository.findByIdMaquinaria(1L))
                .thenReturn(Optional.of(maquinaria));

        boolean resultado = maquinariaServicio.actualizarMaquinaria(1L, actualizada);

        assertThat(resultado).isTrue();
        assertThat(maquinaria.getNombre()).isEqualTo("Bici Pro");
        assertThat(maquinaria.getCantidadTotal()).isEqualTo(20);
        assertThat(maquinaria.getTipoActividad()).isEqualTo(TipoActividad.FUERZA);
        assertThat(maquinaria.getDescripcion()).isEqualTo("Nueva descripción");

        verify(maquinariaRepository).save(maquinaria);
    }

    @Test
    void actualizarMaquinaria_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> maquinariaServicio.actualizarMaquinaria(null, maquinaria))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idMaquinaria no puede ser nulo.");
    }

    @Test
    void actualizarMaquinaria_lanzaExcepcionSiActualizadaEsNula() {
        assertThatThrownBy(() -> maquinariaServicio.actualizarMaquinaria(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Los datos actualizados no pueden ser nulos.");
    }

    @Test
    void actualizarMaquinaria_lanzaExcepcionSiNoExiste() {
        when(maquinariaRepository.findByIdMaquinaria(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> maquinariaServicio.actualizarMaquinaria(1L, new Maquinaria()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe maquinaria con id: 1");
    }

    // ---------------------------------------------------------
    // eliminarMaquinaria
    // ---------------------------------------------------------

    @Test
    void eliminarMaquinaria_deberiaEliminarSiExiste() {
        when(maquinariaRepository.existsById(1L)).thenReturn(true);

        boolean resultado = maquinariaServicio.eliminarMaquinaria(1L);

        assertThat(resultado).isTrue();
        verify(maquinariaRepository).deleteById(1L);
    }

    @Test
    void eliminarMaquinaria_deberiaRetornarFalseSiNoExiste() {
        when(maquinariaRepository.existsById(1L)).thenReturn(false);

        boolean resultado = maquinariaServicio.eliminarMaquinaria(1L);

        assertThat(resultado).isFalse();
        verify(maquinariaRepository, never()).deleteById(any());
    }

    @Test
    void eliminarMaquinaria_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> maquinariaServicio.eliminarMaquinaria(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idMaquinaria no puede ser nulo.");
    }

    // ---------------------------------------------------------
    // obtenerMaquinariaPorId
    // ---------------------------------------------------------

    @Test
    void obtenerMaquinariaPorId_deberiaDevolverMaquinaria() {
        when(maquinariaRepository.findByIdMaquinaria(1L))
                .thenReturn(Optional.of(maquinaria));

        Maquinaria resultado = maquinariaServicio.obtenerMaquinariaPorId(1L);

        assertThat(resultado).isEqualTo(maquinaria);
    }

    @Test
    void obtenerMaquinariaPorId_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> maquinariaServicio.obtenerMaquinariaPorId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idMaquinaria no puede ser nulo.");
    }

    @Test
    void obtenerMaquinariaPorId_lanzaExcepcionSiNoExiste() {
        when(maquinariaRepository.findByIdMaquinaria(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> maquinariaServicio.obtenerMaquinariaPorId(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe maquinaria con id: 1");
    }

    // ---------------------------------------------------------
    // obtenerPorNombre
    // ---------------------------------------------------------

    @Test
    void obtenerPorNombre_deberiaDevolverMaquinaria() {
        when(maquinariaRepository.findByNombre("Bicicleta"))
                .thenReturn(Optional.of(maquinaria));

        Maquinaria resultado = maquinariaServicio.obtenerPorNombre("Bicicleta");

        assertThat(resultado).isEqualTo(maquinaria);
    }

    @Test
    void obtenerPorNombre_lanzaExcepcionSiNombreEsVacioONulo() {
        assertThatThrownBy(() -> maquinariaServicio.obtenerPorNombre(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El nombre no puede ser nulo o vacío.");

        assertThatThrownBy(() -> maquinariaServicio.obtenerPorNombre(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El nombre no puede ser nulo o vacío.");
    }

    @Test
    void obtenerPorNombre_lanzaExcepcionSiNoExiste() {
        when(maquinariaRepository.findByNombre("Bicicleta"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> maquinariaServicio.obtenerPorNombre("Bicicleta"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe maquinaria con nombre: Bicicleta");
    }

    // ---------------------------------------------------------
    // listarTodas
    // ---------------------------------------------------------

    @Test
    void listarTodas_deberiaRetornarLista() {
        when(maquinariaRepository.findAll()).thenReturn(List.of(maquinaria));

        List<Maquinaria> lista = maquinariaServicio.listarTodas();

        assertThat(lista).containsExactly(maquinaria);
    }

    // ---------------------------------------------------------
    // buscarPorTipoActividad
    // ---------------------------------------------------------

    @Test
    void buscarPorTipoActividad_deberiaDevolverMaquinaria() {
        when(maquinariaRepository.findByTipoActividad(TipoActividad.CARDIO))
                .thenReturn(List.of(maquinaria));

        List<Maquinaria> lista = maquinariaServicio.buscarPorTipoActividad(TipoActividad.CARDIO);

        assertThat(lista).containsExactly(maquinaria);
    }

    @Test
    void buscarPorTipoActividad_lanzaExcepcionSiTipoEsNulo() {
        assertThatThrownBy(() -> maquinariaServicio.buscarPorTipoActividad(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El tipo de actividad no puede ser nulo.");
    }

    // ---------------------------------------------------------
    // buscarDisponibles
    // ---------------------------------------------------------

    @Test
    void buscarDisponibles_deberiaFiltrarPorTipoSiNoEsNulo() {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusHours(1);

        Maquinaria m2 = new Maquinaria();
        m2.setTipoActividad(TipoActividad.FUERZA);

        when(maquinariaRepository.findDisponibles(inicio, fin, Estado.Pendiente))
                .thenReturn(List.of(maquinaria, m2));

        List<Maquinaria> resultado =
                maquinariaServicio.buscarDisponibles(inicio, fin, TipoActividad.CARDIO);

        assertThat(resultado).containsExactly(maquinaria);
    }

    @Test
    void buscarDisponibles_sinFiltroDevuelveTodo() {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusHours(1);

        Maquinaria m2 = new Maquinaria();

        when(maquinariaRepository.findDisponibles(inicio, fin, Estado.Pendiente))
                .thenReturn(List.of(maquinaria, m2));

        List<Maquinaria> resultado =
                maquinariaServicio.buscarDisponibles(inicio, fin, null);

        assertThat(resultado).containsExactly(maquinaria, m2);
    }

    @Test
    void buscarDisponibles_lanzaExcepcionSiInicioOuFinEsNulo() {
        assertThatThrownBy(() -> maquinariaServicio.buscarDisponibles(null, LocalDateTime.now(), TipoActividad.CARDIO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El intervalo no puede ser nulo.");

        assertThatThrownBy(() -> maquinariaServicio.buscarDisponibles(LocalDateTime.now(), null, TipoActividad.CARDIO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El intervalo no puede ser nulo.");
    }
}
