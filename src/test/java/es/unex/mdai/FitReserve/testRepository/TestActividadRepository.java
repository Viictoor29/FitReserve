package es.unex.mdai.FitReserve.testRepository;

import es.unex.mdai.FitReserve.data.enume.NivelActividad;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Actividad;
import es.unex.mdai.FitReserve.data.repository.ActividadRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class ActividadRepositoryTest {

    @Autowired
    private ActividadRepository repo;

    @Test
    @DisplayName("save() crea y actualiza una actividad correctamente")
    void save_create_and_update_ok() {
        // CREATE
        Actividad nueva = new Actividad();
        nueva.setNombre("Crossfit Básico");
        nueva.setDescripcion("Sesión introductoria de Crossfit");
        nueva.setTipoActividad(TipoActividad.FUERZA);
        nueva.setNivel(NivelActividad.NORMAL);

        Actividad guardada = repo.save(nueva);
        assertThat(guardada.getIdActividad()).isNotNull();

        // READ recién creada
        Actividad recuperada = repo.findByIdActividad(guardada.getIdActividad()).orElseThrow();
        assertThat(recuperada.getNombre()).isEqualTo("Crossfit Básico");

        // UPDATE
        recuperada.setDescripcion("Clase modificada");
        repo.save(recuperada);

        Actividad modificada = repo.findByIdActividad(guardada.getIdActividad()).orElseThrow();
        assertThat(modificada.getDescripcion()).isEqualTo("Clase modificada");
    }

    @Test
    @DisplayName("findAll() devuelve las actividades existentes")
    void findAll_ok() {
        List<Actividad> all = repo.findAll();
        assertThat(all).isNotEmpty();
        // (Opcional) útil si tienes las del seed: Yoga Vinyasa y HIIT
        assertThat(all).extracting(Actividad::getNombre)
                .contains("Yoga Vinyasa", "HIIT");
    }

    @Test
    @DisplayName("findByTipoActividad(CARDIO) contiene HIIT")
    void findByTipo_cardio_ok() {
        var list = repo.findByTipoActividad(TipoActividad.CARDIO);
        assertThat(list).isNotEmpty();
        assertThat(list).extracting(Actividad::getNombre).contains("HIIT");
    }

    @Test
    @DisplayName("findByNivel(AMATEUR) contiene Yoga Vinyasa")
    void findByNivel_amateur_ok() {
        var list = repo.findByNivel(NivelActividad.AMATEUR);
        assertThat(list).isNotEmpty();
        assertThat(list).extracting(Actividad::getNombre).contains("Yoga Vinyasa");
    }

    @Test
    @DisplayName("findByIdActividad: presente y ausente")
    void findByIdActividad_present_absent() {
        // Tomamos un id real para no depender de autogenerados
        var yoga = repo.findAll().stream()
                .filter(a -> "Yoga Vinyasa".equals(a.getNombre()))
                .findFirst().orElseThrow();

        assertThat(repo.findByIdActividad(yoga.getIdActividad()))
                .isPresent()
                .get()
                .extracting(Actividad::getNombre)
                .isEqualTo("Yoga Vinyasa");

        assertThat(repo.findByIdActividad(9_999_999L)).isNotPresent();
    }

    @Test
    @DisplayName("deleteByIdActividad elimina la actividad por id")
    void deleteByIdActividad_ok() {
        var hiit = repo.findAll().stream()
                .filter(a -> "HIIT".equals(a.getNombre()))
                .findFirst().orElseThrow();

        repo.deleteByIdActividad(hiit.getIdActividad());
        repo.flush();

        assertThat(repo.findByIdActividad(hiit.getIdActividad())).isNotPresent();
        assertThat(repo.findAll()).extracting(Actividad::getNombre).doesNotContain("HIIT");
    }
}
