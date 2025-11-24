package es.unex.mdai.FitReserve.testServices;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.*;
import es.unex.mdai.FitReserve.data.repository.EntrenadorRepository;
import es.unex.mdai.FitReserve.data.repository.MaquinariaRepository;
import es.unex.mdai.FitReserve.data.repository.ReservaMaquinariaRepository;
import es.unex.mdai.FitReserve.data.repository.ReservaRepository;
import es.unex.mdai.FitReserve.services.ReservaServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestReservaServicioImpl {

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private ReservaMaquinariaRepository reservaMaquinariaRepository;
    @Mock
    private MaquinariaRepository maquinariaRepository;
    @Mock
    private EntrenadorRepository entrenadorRepository;

    @InjectMocks
    private ReservaServicioImpl reservaServicio;

    private Reserva reservaBase;
    private Cliente cliente;
    private Entrenador entrenador;
    private Sala sala;
    private Actividad actividad;

    @BeforeEach
    void setUp() {
        LocalDateTime inicio = LocalDateTime.now().plusHours(2);
        LocalDateTime fin = inicio.plusHours(1);

        cliente = new Cliente();
        cliente.setIdCliente(10L);

        entrenador = new Entrenador();
        entrenador.setIdEntrenador(20L);

        sala = new Sala();
        sala.setIdSala(30L);

        actividad = new Actividad();
        actividad.setIdActividad(40L);
        actividad.setTipoActividad(TipoActividad.CARDIO);

        reservaBase = new Reserva();
        reservaBase.setIdReserva(1L);
        reservaBase.setFechaHoraInicio(inicio);
        reservaBase.setFechaHoraFin(fin);
        reservaBase.setCliente(cliente);
        reservaBase.setEntrenador(entrenador);
        reservaBase.setSala(sala);
        reservaBase.setActividad(actividad);
        reservaBase.setEstado(Estado.Pendiente);
    }

    // ---------------------------------------------------------
    // crearReserva
    // ---------------------------------------------------------

    @Test
    void crearReserva_deberiaCrearSiTodoEsValidoYSinMaquinaria() {
        Reserva r = copiarReservaBase();
        r.setMaquinariaAsignada(null);

        when(reservaRepository.existeSolapeSala(eq(30L), any(), any(), eq(Estado.Pendiente)))
                .thenReturn(false);
        when(reservaRepository.existeSolapeEntrenador(eq(20L), any(), any(), eq(Estado.Pendiente)))
                .thenReturn(false);

        boolean resultado = reservaServicio.crearReserva(r);

        assertThat(resultado).isTrue();
        assertThat(r.getEstado()).isEqualTo(Estado.Pendiente);
        verify(reservaRepository).save(r);
    }

    @Test
    void crearReserva_deberiaRetornarFalseSiReservaEsNula() {
        boolean resultado = reservaServicio.crearReserva(null);
        assertThat(resultado).isFalse();
    }

    @Test
    void crearReserva_deberiaRetornarFalseSiFaltanCamposObligatorios() {
        Reserva r = new Reserva(); // sin nada
        boolean resultado = reservaServicio.crearReserva(r);
        assertThat(resultado).isFalse();
    }

    @Test
    void crearReserva_deberiaRetornarFalseSiFinNoEsPosteriorAInicio() {
        Reserva r = copiarReservaBase();
        r.setFechaHoraFin(r.getFechaHoraInicio()); // misma hora

        boolean resultado = reservaServicio.crearReserva(r);
        assertThat(resultado).isFalse();
    }

    @Test
    void crearReserva_deberiaRetornarFalseSiEsEnElPasado() {
        Reserva r = copiarReservaBase();
        r.setFechaHoraInicio(LocalDateTime.now().minusHours(2));
        r.setFechaHoraFin(LocalDateTime.now().minusHours(1));

        boolean resultado = reservaServicio.crearReserva(r);
        assertThat(resultado).isFalse();
    }

    @Test
    void crearReserva_deberiaRetornarFalseSiHaySolapeSala() {
        Reserva r = copiarReservaBase();

        when(reservaRepository.existeSolapeSala(eq(30L), any(), any(), eq(Estado.Pendiente)))
                .thenReturn(true);

        boolean resultado = reservaServicio.crearReserva(r);
        assertThat(resultado).isFalse();
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void crearReserva_deberiaRetornarFalseSiHaySolapeEntrenador() {
        Reserva r = copiarReservaBase();

        when(reservaRepository.existeSolapeSala(eq(30L), any(), any(), eq(Estado.Pendiente)))
                .thenReturn(false);
        when(reservaRepository.existeSolapeEntrenador(eq(20L), any(), any(), eq(Estado.Pendiente)))
                .thenReturn(true);

        boolean resultado = reservaServicio.crearReserva(r);
        assertThat(resultado).isFalse();
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void crearReserva_deberiaRetornarFalseSiNoHaySuficienteMaquinaria() {
        // Reserva con una maquinaria
        Reserva r = copiarReservaBase();

        Maquinaria maquinaria = new Maquinaria();
        maquinaria.setIdMaquinaria(99L);
        maquinaria.setCantidadTotal(5);

        ReservaMaquinaria rm = new ReservaMaquinaria();
        rm.setReserva(r);
        rm.setMaquinaria(maquinaria);
        rm.setCantidad(4); // pedimos 4

        r.setMaquinariaAsignada(List.of(rm));

        when(reservaRepository.existeSolapeSala(eq(30L), any(), any(), eq(Estado.Pendiente)))
                .thenReturn(false);
        when(reservaRepository.existeSolapeEntrenador(eq(20L), any(), any(), eq(Estado.Pendiente)))
                .thenReturn(false);

        // totalEnUso = 3 -> 3 + 4 > 5 => no disponible
        when(reservaMaquinariaRepository.totalReservadoEnIntervalo(eq(99L), any(), any(), eq(Estado.Pendiente)))
                .thenReturn(3);
        when(reservaMaquinariaRepository.totalReservadoEnIntervalo(eq(99L), any(), any(), eq(Estado.Completada)))
                .thenReturn(0);

        when(maquinariaRepository.findById(99L))
                .thenReturn(Optional.of(maquinaria));

        boolean resultado = reservaServicio.crearReserva(r);
        assertThat(resultado).isFalse();
        verify(reservaRepository, never()).save(any());
    }

    // ---------------------------------------------------------
    // actualizarReserva
    // ---------------------------------------------------------

    @Test
    void actualizarReserva_deberiaActualizarSiNoHaEmpezado() {
        Reserva existente = copiarReservaBase();
        existente.setFechaHoraInicio(LocalDateTime.now().plusHours(3));
        existente.setFechaHoraFin(LocalDateTime.now().plusHours(4));

        Reserva cambios = new Reserva();
        cambios.setComentarios("Nuevo comentario");

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(existente));

        boolean resultado = reservaServicio.actualizarReserva(1L, cambios);

        assertThat(resultado).isTrue();
        assertThat(existente.getComentarios()).isEqualTo("Nuevo comentario");
        verify(reservaRepository).save(existente);
    }

    @Test
    void actualizarReserva_retornaFalseSiIdONuevaEsNulo() {
        assertThat(reservaServicio.actualizarReserva(null, new Reserva())).isFalse();
        assertThat(reservaServicio.actualizarReserva(1L, null)).isFalse();
    }

    @Test
    void actualizarReserva_retornaFalseSiNoExiste() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        boolean resultado = reservaServicio.actualizarReserva(1L, new Reserva());
        assertThat(resultado).isFalse();
    }

    @Test
    void actualizarReserva_retornaFalseSiYaHaEmpezado() {
        Reserva existente = copiarReservaBase();
        existente.setFechaHoraInicio(LocalDateTime.now().minusHours(1));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(existente));

        boolean resultado = reservaServicio.actualizarReserva(1L, new Reserva());
        assertThat(resultado).isFalse();
    }

    // ---------------------------------------------------------
    // eliminarReserva
    // ---------------------------------------------------------

    @Test
    void eliminarReserva_deberiaEliminarSiExiste() {
        when(reservaRepository.existsById(1L)).thenReturn(true);

        boolean resultado = reservaServicio.eliminarReserva(1L);

        assertThat(resultado).isTrue();
        verify(reservaRepository).deleteByIdReserva(1L);
    }

    @Test
    void eliminarReserva_retornaFalseSiIdEsNulo() {
        assertThat(reservaServicio.eliminarReserva(null)).isFalse();
    }

    @Test
    void eliminarReserva_retornaFalseSiNoExiste() {
        when(reservaRepository.existsById(1L)).thenReturn(false);

        boolean resultado = reservaServicio.eliminarReserva(1L);
        assertThat(resultado).isFalse();
    }

    // ---------------------------------------------------------
    // obtenerPorId
    // ---------------------------------------------------------

    @Test
    void obtenerPorId_deberiaDevolverReserva() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaBase));

        Reserva r = reservaServicio.obtenerPorId(1L);
        assertThat(r).isEqualTo(reservaBase);
    }

    @Test
    void obtenerPorId_retornaNullSiIdEsNulo() {
        assertThat(reservaServicio.obtenerPorId(null)).isNull();
    }

    @Test
    void obtenerPorId_retornaNullSiNoExiste() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        Reserva r = reservaServicio.obtenerPorId(1L);
        assertThat(r).isNull();
    }

    // ---------------------------------------------------------
    // cancelarPorCliente
    // ---------------------------------------------------------

    @Test
    void cancelarPorCliente_deberiaCancelarSiTodoOk() {
        Reserva r = copiarReservaBase();
        r.setEstado(Estado.Pendiente);
        r.setFechaHoraInicio(LocalDateTime.now().plusHours(2));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

        boolean resultado = reservaServicio.cancelarPorCliente(1L, 10L);

        assertThat(resultado).isTrue();
        assertThat(r.getEstado()).isEqualTo(Estado.Cancelada);
        verify(reservaRepository).save(r);
    }

    @Test
    void cancelarPorCliente_retornaFalseSiParametrosNulos() {
        assertThat(reservaServicio.cancelarPorCliente(null, 10L)).isFalse();
        assertThat(reservaServicio.cancelarPorCliente(1L, null)).isFalse();
    }

    @Test
    void cancelarPorCliente_retornaFalseSiNoExiste() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThat(reservaServicio.cancelarPorCliente(1L, 10L)).isFalse();
    }

    @Test
    void cancelarPorCliente_retornaFalseSiNoEsDelCliente() {
        Reserva r = copiarReservaBase();
        r.getCliente().setIdCliente(999L);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

        assertThat(reservaServicio.cancelarPorCliente(1L, 10L)).isFalse();
    }

    @Test
    void cancelarPorCliente_retornaFalseSiYaEmpezada() {
        Reserva r = copiarReservaBase();
        r.setFechaHoraInicio(LocalDateTime.now().minusMinutes(10));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

        assertThat(reservaServicio.cancelarPorCliente(1L, 10L)).isFalse();
    }

    @Test
    void cancelarPorCliente_retornaFalseSiYaCanceladaOCompletada() {
        Reserva r = copiarReservaBase();
        r.setFechaHoraInicio(LocalDateTime.now().plusHours(1));
        r.setEstado(Estado.Cancelada);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

        assertThat(reservaServicio.cancelarPorCliente(1L, 10L)).isFalse();
    }

    // ---------------------------------------------------------
    // cancelarPorEntrenador
    // ---------------------------------------------------------

    @Test
    void cancelarPorEntrenador_deberiaCancelarSiTodoOk() {
        Reserva r = copiarReservaBase();
        r.setEstado(Estado.Pendiente);
        r.setFechaHoraInicio(LocalDateTime.now().plusHours(2));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

        boolean resultado = reservaServicio.cancelarPorEntrenador(1L, 20L);

        assertThat(resultado).isTrue();
        assertThat(r.getEstado()).isEqualTo(Estado.Cancelada);
        verify(reservaRepository).save(r);
    }

    @Test
    void cancelarPorEntrenador_retornaFalseSiParametrosNulos() {
        assertThat(reservaServicio.cancelarPorEntrenador(null, 20L)).isFalse();
        assertThat(reservaServicio.cancelarPorEntrenador(1L, null)).isFalse();
    }

    @Test
    void cancelarPorEntrenador_retornaFalseSiNoExiste() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThat(reservaServicio.cancelarPorEntrenador(1L, 20L)).isFalse();
    }

    @Test
    void cancelarPorEntrenador_retornaFalseSiNoEsDelEntrenador() {
        Reserva r = copiarReservaBase();
        r.getEntrenador().setIdEntrenador(999L);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));
        assertThat(reservaServicio.cancelarPorEntrenador(1L, 20L)).isFalse();
    }

    // ---------------------------------------------------------
    // marcarComoCompletada
    // ---------------------------------------------------------

    @Test
    void marcarComoCompletada_deberiaMarcarSiTodoOk() {
        Reserva r = copiarReservaBase();
        r.setEstado(Estado.Pendiente);
        r.setFechaHoraInicio(LocalDateTime.now().minusHours(2));
        r.setFechaHoraFin(LocalDateTime.now().minusHours(1));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

        boolean resultado = reservaServicio.marcarComoCompletada(1L, 20L);

        assertThat(resultado).isTrue();
        assertThat(r.getEstado()).isEqualTo(Estado.Completada);
        verify(reservaRepository).save(r);
    }

    @Test
    void marcarComoCompletada_retornaFalseSiParametrosNulos() {
        assertThat(reservaServicio.marcarComoCompletada(null, 20L)).isFalse();
        assertThat(reservaServicio.marcarComoCompletada(1L, null)).isFalse();
    }

    @Test
    void marcarComoCompletada_retornaFalseSiNoExiste() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThat(reservaServicio.marcarComoCompletada(1L, 20L)).isFalse();
    }

    @Test
    void marcarComoCompletada_retornaFalseSiNoEsDelEntrenador() {
        Reserva r = copiarReservaBase();
        r.getEntrenador().setIdEntrenador(999L);
        r.setFechaHoraInicio(LocalDateTime.now().minusHours(2));
        r.setFechaHoraFin(LocalDateTime.now().minusHours(1));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));
        assertThat(reservaServicio.marcarComoCompletada(1L, 20L)).isFalse();
    }

    @Test
    void marcarComoCompletada_retornaFalseSiAunNoHaFinalizado() {
        Reserva r = copiarReservaBase();
        r.setFechaHoraInicio(LocalDateTime.now().plusMinutes(10));
        r.setFechaHoraFin(LocalDateTime.now().plusHours(2));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));
        assertThat(reservaServicio.marcarComoCompletada(1L, 20L)).isFalse();
    }

    @Test
    void marcarComoCompletada_retornaFalseSiEstadoNoEsPendiente() {
        Reserva r = copiarReservaBase();
        r.setEstado(Estado.Cancelada);
        r.setFechaHoraInicio(LocalDateTime.now().minusHours(2));
        r.setFechaHoraFin(LocalDateTime.now().minusHours(1));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));
        assertThat(reservaServicio.marcarComoCompletada(1L, 20L)).isFalse();
    }

    // ---------------------------------------------------------
    // listados / próximas
    // ---------------------------------------------------------

    @Test
    void listarHistorialCliente_deberiaDelegarEnRepositorio() {
        when(reservaRepository.findByClienteIdClienteOrderByFechaHoraInicioDesc(10L))
                .thenReturn(List.of(reservaBase));

        List<Reserva> lista = reservaServicio.listarHistorialCliente(10L);
        assertThat(lista).containsExactly(reservaBase);
    }

    @Test
    void listarHistorialCliente_conIdNuloDevuelveListaVacia() {
        assertThat(reservaServicio.listarHistorialCliente(null)).isEmpty();
    }

    @Test
    void listarHistorialEntrenador_deberiaDelegarEnRepositorio() {
        when(reservaRepository.findByEntrenadorIdEntrenadorOrderByFechaHoraInicioDesc(20L))
                .thenReturn(List.of(reservaBase));

        List<Reserva> lista = reservaServicio.listarHistorialEntrenador(20L);
        assertThat(lista).containsExactly(reservaBase);
    }

    @Test
    void listarProximasCliente_deberiaDelegarEnRepositorio() {
        when(reservaRepository.findByClienteIdClienteAndFechaHoraInicioAfter(eq(10L), any()))
                .thenReturn(List.of(reservaBase));

        List<Reserva> lista = reservaServicio.listarProximasCliente(10L);
        assertThat(lista).containsExactly(reservaBase);
    }

    @Test
    void listarProximasEntrenador_deberiaDelegarEnRepositorio() {
        when(reservaRepository.findByEntrenadorIdEntrenadorAndFechaHoraInicioAfter(eq(20L), any()))
                .thenReturn(List.of(reservaBase));

        List<Reserva> lista = reservaServicio.listarProximasEntrenador(20L);
        assertThat(lista).containsExactly(reservaBase);
    }

    @Test
    void obtenerProximaReservaCliente_deberiaDevolverPrimeraSiHay() {
        when(reservaRepository.proximaReservaCliente(eq(10L), any(), any(Pageable.class)))
                .thenReturn(List.of(reservaBase));

        Reserva r = reservaServicio.obtenerProximaReservaCliente(10L);
        assertThat(r).isEqualTo(reservaBase);
    }

    @Test
    void obtenerProximaReservaCliente_retornaNullSiNoHay() {
        when(reservaRepository.proximaReservaCliente(eq(10L), any(), any(Pageable.class)))
                .thenReturn(List.of());

        Reserva r = reservaServicio.obtenerProximaReservaCliente(10L);
        assertThat(r).isNull();
    }

    @Test
    void obtenerProximaClaseEntrenador_deberiaDevolverPrimeraSiHay() {
        when(reservaRepository.proximaClaseEntrenador(eq(20L), any(), any(Pageable.class)))
                .thenReturn(List.of(reservaBase));

        Reserva r = reservaServicio.obtenerProximaClaseEntrenador(20L);
        assertThat(r).isEqualTo(reservaBase);
    }

    @Test
    void obtenerProximaClaseEntrenador_retornaNullSiNoHay() {
        when(reservaRepository.proximaClaseEntrenador(eq(20L), any(), any(Pageable.class)))
                .thenReturn(List.of());

        Reserva r = reservaServicio.obtenerProximaClaseEntrenador(20L);
        assertThat(r).isNull();
    }

    // ---------------------------------------------------------
    // solapes
    // ---------------------------------------------------------

    @Test
    void haySolapeSala_deberiaDelegarYRetornarValor() {
        when(reservaRepository.existeSolapeSala(eq(30L), any(), any(), eq(Estado.Pendiente)))
                .thenReturn(true);

        boolean res = reservaServicio.haySolapeSala(30L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertThat(res).isTrue();
    }

    @Test
    void haySolapeSala_conParametrosNulosDevuelveFalse() {
        assertThat(reservaServicio.haySolapeSala(null, LocalDateTime.now(), LocalDateTime.now())).isFalse();
        assertThat(reservaServicio.haySolapeSala(1L, null, LocalDateTime.now())).isFalse();
        assertThat(reservaServicio.haySolapeSala(1L, LocalDateTime.now(), null)).isFalse();
    }

    @Test
    void haySolapeEntrenador_deberiaDelegarYRetornarValor() {
        when(reservaRepository.existeSolapeEntrenador(eq(20L), any(), any(), eq(Estado.Pendiente)))
                .thenReturn(true);

        boolean res = reservaServicio.haySolapeEntrenador(20L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertThat(res).isTrue();
    }

    @Test
    void haySolapeEntrenador_conParametrosNulosDevuelveFalse() {
        assertThat(reservaServicio.haySolapeEntrenador(null, LocalDateTime.now(), LocalDateTime.now())).isFalse();
        assertThat(reservaServicio.haySolapeEntrenador(1L, null, LocalDateTime.now())).isFalse();
        assertThat(reservaServicio.haySolapeEntrenador(1L, LocalDateTime.now(), null)).isFalse();
    }

    // ---------------------------------------------------------
    // maquinaria asociada
    // ---------------------------------------------------------

    @Test
    void obtenerMaquinariaDeReserva_deberiaDelegar() {
        ReservaMaquinaria rm = new ReservaMaquinaria();
        when(reservaMaquinariaRepository.findByReservaIdReserva(1L))
                .thenReturn(List.of(rm));

        List<ReservaMaquinaria> lista = reservaServicio.obtenerMaquinariaDeReserva(1L);
        assertThat(lista).containsExactly(rm);
    }

    @Test
    void obtenerMaquinariaDeReserva_conIdNuloDevuelveListaVacia() {
        assertThat(reservaServicio.obtenerMaquinariaDeReserva(null)).isEmpty();
    }

    @Test
    void eliminarMaquinariaDeReserva_deberiaDevolverTrueSiNoHayExcepcion() {
        boolean res = reservaServicio.eliminarMaquinariaDeReserva(1L);
        assertThat(res).isTrue();
        verify(reservaMaquinariaRepository).deleteByReservaIdReserva(1L);
    }

    @Test
    void eliminarMaquinariaDeReserva_deberiaDevolverFalseSiIdEsNulo() {
        assertThat(reservaServicio.eliminarMaquinariaDeReserva(null)).isFalse();
        verify(reservaMaquinariaRepository, never()).deleteByReservaIdReserva(any());
    }

    @Test
    void eliminarMaquinariaDeReserva_deberiaDevolverFalseSiLanzaExcepcion() {
        doThrow(new RuntimeException("error"))
                .when(reservaMaquinariaRepository).deleteByReservaIdReserva(1L);

        boolean res = reservaServicio.eliminarMaquinariaDeReserva(1L);
        assertThat(res).isFalse();
    }

    // ---------------------------------------------------------
    // totalMaquinariaReservadaEnIntervalo
    // ---------------------------------------------------------

    @Test
    void totalMaquinariaReservadaEnIntervalo_sumaPendienteYCompletada() {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusHours(1);

        when(reservaMaquinariaRepository.totalReservadoEnIntervalo(1L, inicio, fin, Estado.Pendiente))
                .thenReturn(3);
        when(reservaMaquinariaRepository.totalReservadoEnIntervalo(1L, inicio, fin, Estado.Completada))
                .thenReturn(2);

        int total = reservaServicio.totalMaquinariaReservadaEnIntervalo(
                1L, inicio, fin, TipoActividad.CARDIO
        );

        assertThat(total).isEqualTo(5);
    }

    @Test
    void totalMaquinariaReservadaEnIntervalo_devuelve0SiParametrosNulos() {
        assertThat(reservaServicio.totalMaquinariaReservadaEnIntervalo(null, LocalDateTime.now(), LocalDateTime.now(), null)).isEqualTo(0);
        assertThat(reservaServicio.totalMaquinariaReservadaEnIntervalo(1L, null, LocalDateTime.now(), null)).isEqualTo(0);
        assertThat(reservaServicio.totalMaquinariaReservadaEnIntervalo(1L, LocalDateTime.now(), null, null)).isEqualTo(0);
    }

    // ---------------------------------------------------------
    // helpers
    // ---------------------------------------------------------

    private Reserva copiarReservaBase() {
        Reserva r = new Reserva();
        r.setIdReserva(reservaBase.getIdReserva());
        r.setFechaHoraInicio(reservaBase.getFechaHoraInicio());
        r.setFechaHoraFin(reservaBase.getFechaHoraFin());
        r.setCliente(cliente);
        r.setEntrenador(entrenador);
        r.setSala(sala);
        r.setActividad(actividad);
        r.setEstado(reservaBase.getEstado());
        return r;
    }
}
