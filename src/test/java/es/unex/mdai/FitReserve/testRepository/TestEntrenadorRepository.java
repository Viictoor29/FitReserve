package es.unex.mdai.FitReserve.testRepository;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.Entrenador;
import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.data.repository.EntrenadorRepository;
import es.unex.mdai.FitReserve.data.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class EntrenadorRepositoryTest {

    @Autowired
    private EntrenadorRepository repo;

    @Autowired
    private UsuarioRepository usuarios;

    @Test
    @DisplayName("findByUsuarioIdUsuario devuelve el entrenador asociado al usuario (PK=FK)")
    void findByUsuarioIdUsuario_ok() {
        Long sofiaId = usuarios.findByEmail("sofia@gym.test").map(Usuario::getIdUsuario).orElseThrow();
        Long davidId = usuarios.findByEmail("david@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        var eSofia = repo.findByUsuarioIdUsuario(sofiaId);
        var eDavid = repo.findByUsuarioIdUsuario(davidId);

        assertThat(eSofia).isPresent();
        assertThat(eDavid).isPresent();
        assertThat(eSofia.get().getIdEntrenador()).isEqualTo(sofiaId);
        assertThat(eDavid.get().getIdEntrenador()).isEqualTo(davidId);
    }

    @Test
    @DisplayName("findByIdEntrenador: presente y ausente")
    void findByIdEntrenador_present_absent() {
        Long sofiaId = usuarios.findByEmail("sofia@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        assertThat(repo.findByIdEntrenador(sofiaId)).isPresent();
        assertThat(repo.findByIdEntrenador(9_999_999L)).isNotPresent();
    }

    @Test
    @DisplayName("findByEspecialidad devuelve los entrenadores de cada especialidad")
    void findByEspecialidad_ok() {
        var yogaPilates = repo.findByEspecialidad("Yoga y Pilates");
        var crossfit    = repo.findByEspecialidad("Crossfit");

        assertThat(yogaPilates).isNotEmpty();
        assertThat(crossfit).isNotEmpty();

        Long sofiaId = usuarios.findByEmail("sofia@gym.test").map(Usuario::getIdUsuario).orElseThrow();
        Long davidId = usuarios.findByEmail("david@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        assertThat(yogaPilates).extracting(Entrenador::getIdEntrenador).contains(sofiaId);
        assertThat(crossfit).extracting(Entrenador::getIdEntrenador).contains(davidId);
    }

    @Test
    @DisplayName("findDisponibles excluye a Sofía por solape (21/10/2025 09:00-10:00)")
    void findDisponibles_excluyeSofia_21Oct_09_10() {
        LocalDateTime inicio = LocalDateTime.of(2025, 10, 21, 9, 0);
        LocalDateTime fin    = LocalDateTime.of(2025, 10, 21, 10, 0);

        Long sofiaId = usuarios.findByEmail("sofia@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        var estadosOcupados = List.of(Estado.Pendiente, Estado.Completada);
        var disponibles = repo.findDisponibles(inicio, fin, estadosOcupados, null);

        assertThat(disponibles)
                .extracting(Entrenador::getIdEntrenador)
                .doesNotContain(sofiaId);
    }

    @Test
    @DisplayName("findDisponibles excluye a David por solape (22/10/2025 18:00-19:00)")
    void findDisponibles_excluyeDavid_22Oct_18_19() {
        LocalDateTime inicio = LocalDateTime.of(2025, 10, 22, 18, 0);
        LocalDateTime fin    = LocalDateTime.of(2025, 10, 22, 19, 0);

        Long davidId = usuarios.findByEmail("david@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        var estadosOcupados = List.of(Estado.Pendiente, Estado.Completada);
        var disponibles = repo.findDisponibles(inicio, fin, estadosOcupados, null);

        assertThat(disponibles)
                .extracting(Entrenador::getIdEntrenador)
                .doesNotContain(davidId);
    }

    @Test
    @DisplayName("findDisponibles con filtro de especialidad: 'Crossfit' ocupado → lista vacía")
    void findDisponibles_filtradoEspecialidad_crossfit_ocupado() {
        LocalDateTime inicio = LocalDateTime.of(2025, 10, 22, 18, 0);
        LocalDateTime fin    = LocalDateTime.of(2025, 10, 22, 19, 0);

        var estadosOcupados = List.of(Estado.Pendiente, Estado.Completada);
        var disponiblesCrossfit = repo.findDisponibles(inicio, fin, estadosOcupados, "Crossfit");

        assertThat(disponiblesCrossfit).isEmpty();
    }

    @Test
    @DisplayName("findDisponibles con estados que no coinciden: no excluye a nadie")
    void findDisponibles_estadosNoCoinciden_noExcluye() {
        LocalDateTime inicio = LocalDateTime.of(2025, 10, 22, 18, 0);
        LocalDateTime fin    = LocalDateTime.of(2025, 10, 22, 19, 0);

        // Usamos CANCELADA para que no coincida con las reservas cargadas (PENDIENTE/COMPLETADA)
        var estadosOcupados = List.of(Estado.Cancelada);
        var disponibles = repo.findDisponibles(inicio, fin, estadosOcupados, null);

        Long davidId = usuarios.findByEmail("david@gym.test").map(Usuario::getIdUsuario).orElseThrow();
        Long sofiaId = usuarios.findByEmail("sofia@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        assertThat(disponibles)
                .extracting(Entrenador::getIdEntrenador)
                .contains(davidId, sofiaId);
    }
}