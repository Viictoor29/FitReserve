package es.unex.mdai.FitReserve.testServices;

import es.unex.mdai.FitReserve.data.enume.NivelActividad;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Actividad;
import es.unex.mdai.FitReserve.data.repository.ActividadRepository;
import es.unex.mdai.FitReserve.services.ActividadServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestActividadServicioImpl {

    @Mock
    private ActividadRepository actividadRepository;

    @InjectMocks
    private ActividadServicioImpl actividadServicio;

    private Actividad actividad;

    @BeforeEach
    void setUp() {
        actividad = new Actividad();
        actividad.setIdActividad(1L);
        actividad.setNombre("Cardio Suave");
        actividad.setDescripcion("Sesión de cardio ligera");
        actividad.setTipoActividad(TipoActividad.CARDIO);
        actividad.setNivel(NivelActividad.NORMAL);
    }

    // ---------------------------------------------------------
    // CREAR ACTIVIDAD
    // ---------------------------------------------------------

    @Test
    void crearActividad_deberiaCrearCuandoNoExisteNombreDuplicado() {

        when(actividadRepository.findByNombre("Cardio Suave"))
                .thenReturn(Optional.empty());

        boolean resultado = actividadServicio.crearActividad(actividad);

        assertThat(resultado).isTrue();
        verify(actividadRepository).save(actividad);
    }

    @Test
    void crearActividad_deberiaRetornarFalseSiNombreDuplicado() {

        when(actividadRepository.findByNombre("Cardio Suave"))
                .thenReturn(Optional.of(actividad));

        boolean resultado = actividadServicio.crearActividad(actividad);

        assertThat(resultado).isFalse();
        verify(actividadRepository, never()).save(any());
    }

    @Test
    void crearActividad_deberiaLanzarExcepcionSiEsNula() {
        assertThatThrownBy(() -> actividadServicio.crearActividad(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La actividad no puede ser nula.");
    }


    // ---------------------------------------------------------
    // ACTUALIZAR ACTIVIDAD
    // ---------------------------------------------------------

    @Test
    void actualizarActividad_deberiaActualizarCamposNoNulos() {

        Actividad actualizada = new Actividad();
        actualizada.setNombre("Cardio Intenso");
        actualizada.setNivel(NivelActividad.PROFESIONAL);

        when(actividadRepository.findByIdActividad(1L))
                .thenReturn(Optional.of(actividad));

        boolean resultado = actividadServicio.actualizarActividad(1L, actualizada);

        assertThat(resultado).isTrue();
        assertThat(actividad.getNombre()).isEqualTo("Cardio Intenso");
        assertThat(actividad.getNivel()).isEqualTo(NivelActividad.PROFESIONAL);

        verify(actividadRepository).save(actividad);
    }

    @Test
    void actualizarActividad_deberiaLanzarExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> actividadServicio.actualizarActividad(null, actividad))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idActividad no puede ser nulo.");
    }

    @Test
    void actualizarActividad_deberiaLanzarExcepcionSiActualizadaEsNula() {
        assertThatThrownBy(() -> actividadServicio.actualizarActividad(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Los datos actualizados no pueden ser nulos.");
    }

    @Test
    void actualizarActividad_deberiaLanzarExcepcionSiNoExiste() {

        when(actividadRepository.findByIdActividad(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> actividadServicio.actualizarActividad(1L, new Actividad()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe actividad con id: 1");
    }


    // ---------------------------------------------------------
    // ELIMINAR
    // ---------------------------------------------------------

    @Test
    void eliminarActividad_deberiaEliminarSiExiste() {

        when(actividadRepository.existsById(1L)).thenReturn(true);

        boolean resultado = actividadServicio.eliminarActividad(1L);

        assertThat(resultado).isTrue();
        verify(actividadRepository).deleteByIdActividad(1L);
    }

    @Test
    void eliminarActividad_deberiaRetornarFalseSiNoExiste() {

        when(actividadRepository.existsById(1L)).thenReturn(false);

        boolean resultado = actividadServicio.eliminarActividad(1L);

        assertThat(resultado).isFalse();
        verify(actividadRepository, never()).deleteByIdActividad(any());
    }

    @Test
    void eliminarActividad_deberiaLanzarExcepcionSiIdEsNulo() {

        assertThatThrownBy(() -> actividadServicio.eliminarActividad(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idActividad no puede ser nulo.");
    }


    // ---------------------------------------------------------
    // OBTENER POR ID
    // ---------------------------------------------------------

    @Test
    void obtenerActividadPorId_deberiaDevolverActividad() {

        when(actividadRepository.findByIdActividad(1L))
                .thenReturn(Optional.of(actividad));

        Actividad resultado = actividadServicio.obtenerActividadPorId(1L);

        assertThat(resultado).isEqualTo(actividad);
    }

    @Test
    void obtenerActividadPorId_lanzaExcepcionSiNoExiste() {

        when(actividadRepository.findByIdActividad(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> actividadServicio.obtenerActividadPorId(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe actividad con id: 1");
    }


    // ---------------------------------------------------------
    // LISTAR TODAS
    // ---------------------------------------------------------

    @Test
    void listarTodas_deberiaRetornarLista() {

        when(actividadRepository.findAll())
                .thenReturn(List.of(actividad));

        List<Actividad> lista = actividadServicio.listarTodas();

        assertThat(lista).containsExactly(actividad);
    }


    // ---------------------------------------------------------
    // BUSCAR POR TIPO
    // ---------------------------------------------------------

    @Test
    void buscarPorTipo_deberiaBuscarCorrectamente() {

        when(actividadRepository.findByTipoActividad(TipoActividad.CARDIO))
                .thenReturn(List.of(actividad));

        List<Actividad> lista = actividadServicio.buscarPorTipo(TipoActividad.CARDIO);

        assertThat(lista).containsExactly(actividad);
    }

    @Test
    void buscarPorTipo_deberiaLanzarExceptionSiTipoEsNulo() {

        assertThatThrownBy(() -> actividadServicio.buscarPorTipo(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El tipo de actividad no puede ser nulo.");
    }


    // ---------------------------------------------------------
    // BUSCAR POR NIVEL
    // ---------------------------------------------------------

    @Test
    void buscarPorNivel_deberiaBuscarCorrectamente() {

        when(actividadRepository.findByNivel(NivelActividad.NORMAL))
                .thenReturn(List.of(actividad));

        List<Actividad> lista = actividadServicio.buscarPorNivel(NivelActividad.NORMAL);

        assertThat(lista).containsExactly(actividad);
    }

    @Test
    void buscarPorNivel_lanzaExceptionSiNivelEsNulo() {

        assertThatThrownBy(() -> actividadServicio.buscarPorNivel(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El nivel no puede ser nulo.");
    }


    // ---------------------------------------------------------
    // BUSCAR POR NOMBRE
    // ---------------------------------------------------------

    @Test
    void buscarPorNombre_deberiaEncontrarActividad() {

        when(actividadRepository.findByNombre("Cardio Suave"))
                .thenReturn(Optional.of(actividad));

        Actividad resultado = actividadServicio.buscarPorNombre("Cardio Suave");

        assertThat(resultado).isEqualTo(actividad);
    }

    @Test
    void buscarPorNombre_deberiaLanzarExcepcionSiEsVacio() {

        assertThatThrownBy(() -> actividadServicio.buscarPorNombre(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El nombre no puede ser nulo o vacío.");
    }

    @Test
    void buscarPorNombre_deberiaLanzarExcepcionSiNoExiste() {

        when(actividadRepository.findByNombre("Cardio Suave"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> actividadServicio.buscarPorNombre("Cardio Suave"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe actividad con nombre: Cardio Suave");
    }
}
