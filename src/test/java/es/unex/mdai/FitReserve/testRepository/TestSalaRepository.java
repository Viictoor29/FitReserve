package es.unex.mdai.FitReserve.testRepository;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.Sala;
import es.unex.mdai.FitReserve.data.repository.SalaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SalaRepositoryTest {

    @Autowired
    private SalaRepository repo;

    @Test
    @DisplayName("findAll() devuelve las salas existentes (incluye Sala A y Sala B)")
    void findAll_ok() {
        var all = repo.findAll();
        assertThat(all).isNotEmpty();
        assertThat(all).extracting(Sala::getNombre)
                .contains("Sala A", "Sala B");
    }

    @Test
    @DisplayName("findByIdSala: presente (Sala A) y ausente")
    void findByIdSala_present_absent() {
        Long salaAId = repo.findAll().stream()
                .filter(s -> "Sala A".equals(s.getNombre()))
                .map(Sala::getIdSala)
                .findFirst()
                .orElseThrow();

        assertThat(repo.findByIdSala(salaAId)).isPresent();
        assertThat(repo.findByIdSala(9_999_999L)).isNotPresent();
    }

    /* ========================
       DISPONIBILIDAD (solapes)
       ======================== */

    @Test
    @DisplayName("findDisponibles: excluye Sala A en 21/10/2025 09:00–10:00")
    void findDisponibles_excluyeSalaA_21Oct_09_10() {
        var inicio = LocalDateTime.of(2025, 10, 21, 9, 0);
        var fin    = LocalDateTime.of(2025, 10, 21, 10, 0);

        var libres = repo.findDisponibles(inicio, fin, List.of(Estado.Pendiente, Estado.Completada));

        assertThat(libres).extracting(Sala::getNombre).doesNotContain("Sala A");
        assertThat(libres).extracting(Sala::getNombre).contains("Sala B");
    }

    @Test
    @DisplayName("findDisponibles: excluye Sala B en 22/10/2025 18:00–19:00")
    void findDisponibles_excluyeSalaB_22Oct_18_19() {
        var inicio = LocalDateTime.of(2025, 10, 22, 18, 0);
        var fin    = LocalDateTime.of(2025, 10, 22, 19, 0);

        var libres = repo.findDisponibles(inicio, fin, List.of(Estado.Pendiente, Estado.Completada));

        assertThat(libres).extracting(Sala::getNombre).doesNotContain("Sala B");
        assertThat(libres).extracting(Sala::getNombre).contains("Sala A");
    }

    @Test
    @DisplayName("findDisponibles: con estados sin coincidencia (CANCELADA) no excluye ninguna sala")
    void findDisponibles_estadosNoCoinciden_noExcluye() {
        var inicio = LocalDateTime.of(2025, 10, 22, 18, 0);
        var fin    = LocalDateTime.of(2025, 10, 22, 19, 0);

        var libres = repo.findDisponibles(inicio, fin, List.of(Estado.Cancelada));

        assertThat(libres).extracting(Sala::getNombre).contains("Sala A", "Sala B");
    }

    /* ============
       CRUD seguro
       ============ */

    @Test
    @DisplayName("save + update + deleteByIdSala: CRUD sobre una sala temporal sin reservas")
    void create_update_delete_ok() {
        // CREATE
        Sala nueva = new Sala();
        nueva.setNombre("Sala Temporal");
        nueva.setCapacidad(12);
        nueva.setUbicacion("Planta 3");
        nueva.setDescripcion("Sala de pruebas");
        nueva = repo.save(nueva);

        assertNotNull(nueva.getIdSala());

        // READ
        Sala encontrada = repo.findByIdSala(nueva.getIdSala()).orElseThrow();
        assertEquals("Sala Temporal", encontrada.getNombre());
        assertEquals(12, encontrada.getCapacidad());

        // UPDATE
        encontrada.setCapacidad(16);
        repo.save(encontrada);

        Sala modificada = repo.findByIdSala(nueva.getIdSala()).orElseThrow();
        assertEquals(16, modificada.getCapacidad());

        // DELETE
        repo.deleteByIdSala(modificada.getIdSala());
        repo.flush();

        assertTrue(repo.findByIdSala(modificada.getIdSala()).isEmpty());
        assertThat(repo.findAll()).extracting(Sala::getNombre).doesNotContain("Sala Temporal");
    }
}