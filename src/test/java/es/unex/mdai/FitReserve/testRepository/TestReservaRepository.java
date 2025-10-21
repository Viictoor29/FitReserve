package es.unex.mdai.FitReserve.testRepository;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.Reserva;
import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.data.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReservaRepositoryTest {

    @Autowired private ReservaRepository reservaRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private EntrenadorRepository entrenadorRepo;
    @Autowired private SalaRepository salaRepo;
    @Autowired private ActividadRepository actividadRepo;
    @Autowired private ReservaMaquinariaRepository reservaMaqRepo;

    /* =========================
       LISTADOS ORDENADOS (READ)
       ========================= */

    @Test
    @DisplayName("findByClienteIdClienteOrderByFechaHoraInicioDesc: devuelve reservas de admin y mario ordenadas desc")
    void listarPorCliente_orderDesc_ok() {
        Long adminId = usuarioRepo.findByEmail("admin@gym.test").map(Usuario::getIdUsuario).orElseThrow();
        Long marioId = usuarioRepo.findByEmail("mario@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        var adminList = reservaRepo.findByClienteIdClienteOrderByFechaHoraInicioDesc(adminId);
        var marioList = reservaRepo.findByClienteIdClienteOrderByFechaHoraInicioDesc(marioId);

        assertThat(adminList).isNotEmpty();
        assertThat(marioList).isNotEmpty();

        // Admin tiene la del 21/10 09:00–10:00
        LocalDateTime expectedAdminStart = LocalDateTime.of(2025,10,21,9,0);
        assertEquals(expectedAdminStart, adminList.get(0).getFechaHoraInicio());

        // Mario tiene la del 22/10 18:00–19:00
        LocalDateTime expectedMarioStart = LocalDateTime.of(2025,10,22,18,0);
        assertEquals(expectedMarioStart, marioList.get(0).getFechaHoraInicio());

        // Orden desc: el primero debe ser >= el segundo si existiera
        if (adminList.size() > 1) {
            assertTrue(adminList.get(0).getFechaHoraInicio().isAfter(adminList.get(1).getFechaHoraInicio())
                    || adminList.get(0).getFechaHoraInicio().isEqual(adminList.get(1).getFechaHoraInicio()));
        }
    }

    @Test
    @DisplayName("findByEntrenadorIdEntrenadorOrderByFechaHoraInicioDesc: devuelve reservas de Sofía y David ordenadas desc")
    void listarPorEntrenador_orderDesc_ok() {
        Long sofiaId  = usuarioRepo.findByEmail("sofia@gym.test").map(Usuario::getIdUsuario).orElseThrow();
        Long davidId  = usuarioRepo.findByEmail("david@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        var sofiaList = reservaRepo.findByEntrenadorIdEntrenadorOrderByFechaHoraInicioDesc(sofiaId);
        var davidList = reservaRepo.findByEntrenadorIdEntrenadorOrderByFechaHoraInicioDesc(davidId);

        assertThat(sofiaList).isNotEmpty();
        assertThat(davidList).isNotEmpty();

        assertEquals(LocalDateTime.of(2025,10,21,9,0), sofiaList.get(0).getFechaHoraInicio());
        assertEquals(LocalDateTime.of(2025,10,22,18,0), davidList.get(0).getFechaHoraInicio());
    }

    /* ===================================
       PRÓXIMAS A PARTIR DE UNA FECHA (READ)
       =================================== */

    @Test
    @DisplayName("findByClienteIdClienteAndFechaHoraInicioAfter: filtra por fecha de inicio")
    void futurasPorCliente_ok() {
        Long adminId = usuarioRepo.findByEmail("admin@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        var desdeAntes = reservaRepo.findByClienteIdClienteAndFechaHoraInicioAfter(
                adminId, LocalDateTime.of(2025,10,21,8,0));
        var desdeDespues = reservaRepo.findByClienteIdClienteAndFechaHoraInicioAfter(
                adminId, LocalDateTime.of(2025,10,21,10,1));

        assertThat(desdeAntes).isNotEmpty();    // debería incluir la de las 09:00
        assertThat(desdeDespues).isEmpty();     // después de 10:01 ya no hay más
    }

    @Test
    @DisplayName("findByEntrenadorIdEntrenadorAndFechaHoraInicioAfter: filtra por fecha de inicio")
    void futurasPorEntrenador_ok() {
        Long davidId = usuarioRepo.findByEmail("david@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        var desdeAntes = reservaRepo.findByEntrenadorIdEntrenadorAndFechaHoraInicioAfter(
                davidId, LocalDateTime.of(2025,10,22,17,0));
        var desdeDespues = reservaRepo.findByEntrenadorIdEntrenadorAndFechaHoraInicioAfter(
                davidId, LocalDateTime.of(2025,10,22,19,1));

        assertThat(desdeAntes).isNotEmpty();    // debería incluir 18:00
        assertThat(desdeDespues).isEmpty();     // no hay más después
    }

    /* ==============================
       COMPROBACIÓN DE SOLAPES (READ)
       ============================== */

    @Test
    @DisplayName("existeSolapeSala: true para Sala B en 22/10 18:00–19:00 con estados activos")
    void solapeSala_true() {
        Long salaB = salaRepo.findAll().stream()
                .filter(s -> "Sala B".equals(s.getNombre()))
                .findFirst().orElseThrow().getIdSala();

        boolean solapa = reservaRepo.existeSolapeSala(
                salaB,
                LocalDateTime.of(2025,10,22,18,0),
                LocalDateTime.of(2025,10,22,19,0),
                List.of(Estado.Pendiente, Estado.Completada)
        );
        assertTrue(solapa);
    }

    @Test
    @DisplayName("existeSolapeSala: false si estados no coinciden (CANCELADA)")
    void solapeSala_false_porEstados() {
        Long salaB = salaRepo.findAll().stream()
                .filter(s -> "Sala B".equals(s.getNombre()))
                .findFirst().orElseThrow().getIdSala();

        boolean solapa = reservaRepo.existeSolapeSala(
                salaB,
                LocalDateTime.of(2025,10,22,18,0),
                LocalDateTime.of(2025,10,22,19,0),
                List.of(Estado.Cancelada)
        );
        assertFalse(solapa);
    }

    @Test
    @DisplayName("existeSolapeEntrenador: true para Sofía (21/10 09:00–10:00) y David (22/10 18:00–19:00)")
    void solapeEntrenador_true() {
        Long sofiaId = usuarioRepo.findByEmail("sofia@gym.test").map(Usuario::getIdUsuario).orElseThrow();
        Long davidId = usuarioRepo.findByEmail("david@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        boolean sofiaSolapa = reservaRepo.existeSolapeEntrenador(
                sofiaId,
                LocalDateTime.of(2025,10,21,9,0),
                LocalDateTime.of(2025,10,21,10,0),
                List.of(Estado.Pendiente, Estado.Completada)
        );
        boolean davidSolapa = reservaRepo.existeSolapeEntrenador(
                davidId,
                LocalDateTime.of(2025,10,22,18,0),
                LocalDateTime.of(2025,10,22,19,0),
                List.of(Estado.Pendiente, Estado.Completada)
        );

        assertTrue(sofiaSolapa);
        assertTrue(davidSolapa);
    }

    @Test
    @DisplayName("existeSolapeEntrenador: false si estados no coinciden (CANCELADA)")
    void solapeEntrenador_false_porEstados() {
        Long sofiaId = usuarioRepo.findByEmail("sofia@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        boolean solapa = reservaRepo.existeSolapeEntrenador(
                sofiaId,
                LocalDateTime.of(2025,10,21,9,0),
                LocalDateTime.of(2025,10,21,10,0),
                List.of(Estado.Cancelada)
        );
        assertFalse(solapa);
    }

    /* ================================
       PRÓXIMA RESERVA/CLASE (con top1)
       ================================ */

    @Test
    @DisplayName("proximaReservaCliente: devuelve la siguiente reserva del cliente (top1 ordenado por fecha asc)")
    void proximaReservaCliente_top1_ok() {
        Long adminId = usuarioRepo.findByEmail("admin@gym.test").map(Usuario::getIdUsuario).orElseThrow();
        var lista = reservaRepo.proximaReservaCliente(adminId, LocalDateTime.of(2025,10,21,8,0), PageRequest.of(0,1));

        assertThat(lista).hasSize(1);
        assertEquals(LocalDateTime.of(2025,10,21,9,0), lista.get(0).getFechaHoraInicio());
    }

    @Test
    @DisplayName("proximaClaseEntrenador: devuelve la siguiente clase del entrenador (top1)")
    void proximaClaseEntrenador_top1_ok() {
        Long davidId = usuarioRepo.findByEmail("david@gym.test").map(Usuario::getIdUsuario).orElseThrow();
        var lista = reservaRepo.proximaClaseEntrenador(davidId, LocalDateTime.of(2025,10,22,8,0), PageRequest.of(0,1));

        assertThat(lista).hasSize(1);
        assertEquals(LocalDateTime.of(2025,10,22,18,0), lista.get(0).getFechaHoraInicio());
    }

    @Test
    @DisplayName("proximaReservaCliente: vacío si 'desde' es posterior a todas")
    void proximaReservaCliente_vacia_si_posterior() {
        Long adminId = usuarioRepo.findByEmail("admin@gym.test").map(Usuario::getIdUsuario).orElseThrow();
        var lista = reservaRepo.proximaReservaCliente(adminId, LocalDateTime.of(2025,10,21,23,59), PageRequest.of(0,1));
        assertThat(lista).isEmpty();
    }

    /* =========================
       DELETE (limpio con N:M)
       ========================= */

    @Test
    @DisplayName("deleteByIdReserva elimina la reserva cuando se borran antes sus items N:M")
    void deleteByIdReserva_ok() {
        // Buscamos la reserva de Yoga (admin + 21/10 09:00)
        var r = reservaRepo.findAll().stream()
                .filter(x -> x.getFechaHoraInicio().equals(LocalDateTime.of(2025,10,21,9,0)))
                .findFirst().orElseThrow();

        // Eliminar antes sus items en la join-table para no violar FK
        reservaMaqRepo.deleteByReservaIdReserva(r.getIdReserva());
        reservaRepo.deleteByIdReserva(r.getIdReserva());
        reservaRepo.flush();

        assertTrue(reservaRepo.findById(r.getIdReserva()).isEmpty());
        assertThat(reservaMaqRepo.findByReservaIdReserva(r.getIdReserva())).isEmpty();
    }
}