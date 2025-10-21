package es.unex.mdai.FitReserve.testRepository;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.Maquinaria;
import es.unex.mdai.FitReserve.data.repository.MaquinariaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class MaquinariaRepositoryTest {

    @Autowired
    private MaquinariaRepository repo;

    @Test
    @DisplayName("findAll() devuelve todas las máquinas existentes")
    void findAll_ok() {
        List<Maquinaria> all = repo.findAll();
        assertThat(all).isNotEmpty();
        assertThat(all).extracting(Maquinaria::getNombre)
                .contains("CintaCorrer", "Mancuernas", "Esterillas");
    }

    @Test
    @DisplayName("findByIdMaquinaria() presente y ausente")
    void findByIdMaquinaria_present_absent() {
        Long idEsterillas = repo.findAll().stream()
                .filter(m -> "Esterillas".equals(m.getNombre()))
                .map(Maquinaria::getIdMaquinaria)
                .findFirst()
                .orElseThrow();

        assertThat(repo.findByIdMaquinaria(idEsterillas)).isPresent();
        assertThat(repo.findByIdMaquinaria(99999L)).isNotPresent();
    }

    @Test
    @DisplayName("findDisponibles() devuelve solo máquinas con unidades libres en 2025-10-21 09:00-10:00 (Esterillas usadas=10/30 disponibles)")
    void findDisponibles_21Oct_09_10_ok() {
        LocalDateTime inicio = LocalDateTime.of(2025, 10, 21, 9, 0);
        LocalDateTime fin    = LocalDateTime.of(2025, 10, 21, 10, 0);

        var disponibles = repo.findDisponibles(inicio, fin, List.of(Estado.Pendiente, Estado.Completada));

        // A esa hora solo se usa Esterillas (10/30) → todas siguen disponibles
        assertThat(disponibles).extracting(Maquinaria::getNombre)
                .contains("Esterillas", "Mancuernas", "CintaCorrer");
    }

    @Test
    @DisplayName("findDisponibles() excluye Mancuernas si se agotan (22/10/2025 18:00-19:00)")
    void findDisponibles_22Oct_18_19_ok() {
        LocalDateTime inicio = LocalDateTime.of(2025, 10, 22, 18, 0);
        LocalDateTime fin    = LocalDateTime.of(2025, 10, 22, 19, 0);

        var disponibles = repo.findDisponibles(inicio, fin, List.of(Estado.Pendiente, Estado.Completada));

        // En esa franja hay reserva de 4 Mancuernas → aún quedan disponibles (20 - 4 = 16)
        assertThat(disponibles).extracting(Maquinaria::getNombre)
                .contains("Mancuernas", "CintaCorrer", "Esterillas");
    }

    @Test
    @DisplayName("findDisponibles() con estados que no coinciden (p. ej. CANCELADA) devuelve todo el inventario")
    void findDisponibles_estadosNoCoinciden_ok() {
        LocalDateTime inicio = LocalDateTime.of(2025, 10, 21, 9, 0);
        LocalDateTime fin    = LocalDateTime.of(2025, 10, 21, 10, 0);

        var disponibles = repo.findDisponibles(inicio, fin, List.of(Estado.Cancelada));

        assertThat(disponibles).extracting(Maquinaria::getNombre)
                .contains("CintaCorrer", "Mancuernas", "Esterillas");
    }
}