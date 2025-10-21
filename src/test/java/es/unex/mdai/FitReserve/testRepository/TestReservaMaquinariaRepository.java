package es.unex.mdai.FitReserve.testRepository;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.ReservaMaquinaria;
import es.unex.mdai.FitReserve.data.repository.ReservaMaquinariaRepository;
import es.unex.mdai.FitReserve.data.repository.MaquinariaRepository;
import es.unex.mdai.FitReserve.data.repository.ReservaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class ReservaMaquinariaRepositoryTest {

    @Autowired
    private ReservaMaquinariaRepository repo;

    @Autowired
    private MaquinariaRepository maquinariaRepo;

    @Autowired
    private ReservaRepository reservaRepo;

    @Test
    @DisplayName("findByReservaIdReserva() devuelve la maquinaria asociada a la reserva de Yoga (Esterillas)")
    void findByReservaIdReserva_ok() {
        var yoga = reservaRepo.findAll().stream()
                .filter(r -> r.getActividad().getNombre().equals("Yoga Vinyasa"))
                .findFirst().orElseThrow();

        List<ReservaMaquinaria> relaciones = repo.findByReservaIdReserva(yoga.getIdReserva());
        assertThat(relaciones).isNotEmpty();
        assertThat(relaciones).extracting(rm -> rm.getMaquinaria().getNombre())
                .contains("Esterillas");
    }

    @Test
    @DisplayName("deleteByReservaIdReserva() elimina las relaciones de esa reserva")
    void deleteByReservaIdReserva_ok() {
        var hiit = reservaRepo.findAll().stream()
                .filter(r -> r.getActividad().getNombre().equals("HIIT"))
                .findFirst().orElseThrow();

        repo.deleteByReservaIdReserva(hiit.getIdReserva());
        repo.flush();

        assertThat(repo.findByReservaIdReserva(hiit.getIdReserva())).isEmpty();
    }

    @Test
    @DisplayName("totalReservadoEnIntervalo() devuelve la cantidad reservada en el rango 21/10/2025 09:00-10:00 (Esterillas=10)")
    void totalReservadoEnIntervalo_21Oct_ok() {
        var esterillas = maquinariaRepo.findAll().stream()
                .filter(m -> m.getNombre().equals("Esterillas"))
                .findFirst().orElseThrow();

        LocalDateTime inicio = LocalDateTime.of(2025, 10, 21, 9, 0);
        LocalDateTime fin    = LocalDateTime.of(2025, 10, 21, 10, 0);

        Integer total = repo.totalReservadoEnIntervalo(
                esterillas.getIdMaquinaria(),
                inicio,
                fin,
                List.of(Estado.Pendiente, Estado.Completada)
        );

        assertThat(total).isEqualTo(10);
    }

    @Test
    @DisplayName("totalReservadoEnIntervalo() devuelve la cantidad reservada en el rango 22/10/2025 18:00-19:00 (Mancuernas=4)")
    void totalReservadoEnIntervalo_22Oct_ok() {
        var mancuernas = maquinariaRepo.findAll().stream()
                .filter(m -> m.getNombre().equals("Mancuernas"))
                .findFirst().orElseThrow();

        LocalDateTime inicio = LocalDateTime.of(2025, 10, 22, 18, 0);
        LocalDateTime fin    = LocalDateTime.of(2025, 10, 22, 19, 0);

        Integer total = repo.totalReservadoEnIntervalo(
                mancuernas.getIdMaquinaria(),
                inicio,
                fin,
                List.of(Estado.Pendiente, Estado.Completada)
        );

        assertThat(total).isEqualTo(4);
    }

    @Test
    @DisplayName("totalReservadoEnIntervalo() con estados sin coincidencia devuelve 0")
    void totalReservadoEnIntervalo_estadosNoCoinciden_ok() {
        var mancuernas = maquinariaRepo.findAll().stream()
                .filter(m -> m.getNombre().equals("Mancuernas"))
                .findFirst().orElseThrow();

        LocalDateTime inicio = LocalDateTime.of(2025, 10, 22, 18, 0);
        LocalDateTime fin    = LocalDateTime.of(2025, 10, 22, 19, 0);

        Integer total = repo.totalReservadoEnIntervalo(
                mancuernas.getIdMaquinaria(),
                inicio,
                fin,
                List.of(Estado.Cancelada)
        );

        assertThat(total).isZero();
    }
}
