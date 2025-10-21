package es.unex.mdai.FitReserve.testRepository;

import es.unex.mdai.FitReserve.data.enume.TipoUsuario;
import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.data.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repo;

    /* ==============
       READ BÁSICO
       ============== */

    @Test
    @DisplayName("findAll() devuelve los usuarios existentes")
    void findAll_ok() {
        var all = repo.findAll();
        assertThat(all).isNotEmpty();
        assertThat(all).extracting(Usuario::getEmail)
                .contains("admin@gym.test", "mario@gym.test", "sofia@gym.test", "david@gym.test");
    }

    @Test
    @DisplayName("findByIdUsuario: presente y ausente")
    void findByIdUsuario_present_absent() {
        Long adminId = repo.findByEmail("admin@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        assertThat(repo.findByIdUsuario(adminId)).isPresent();
        assertThat(repo.findByIdUsuario(9_999_999L)).isNotPresent();
    }

    /* ======================
       findByEmail / exists
       ====================== */

    @Test
    @DisplayName("findByEmail: encuentra usuario por email")
    void findByEmail_ok() {
        var u = repo.findByEmail("mario@gym.test");
        assertThat(u).isPresent();
        assertEquals("Mario", u.get().getNombre());
    }

    @Test
    @DisplayName("existsByEmail: devuelve true/false correctamente")
    void existsByEmail_ok() {
        assertTrue(repo.existsByEmail("admin@gym.test"));
        assertFalse(repo.existsByEmail("noexiste@gym.test"));
    }

    /* ==============================
       findByEmailAndContrasenia
       ============================== */

    @Test
    @DisplayName("findByEmailAndContrasenia: login correcto e incorrecto")
    void findByEmailAndContrasenia_ok() {
        var ok = repo.findByEmailAndContrasenia("mario@gym.test", "1234");
        var fail = repo.findByEmailAndContrasenia("mario@gym.test", "wrong");

        assertThat(ok).isPresent();
        assertThat(fail).isNotPresent();
    }

    /* =======================
       findByTipoUsuario
       ======================= */

    @Test
    @DisplayName("findByTipoUsuario: devuelve usuarios por rol")
    void findByTipoUsuario_ok() {
        var admins = repo.findByTipoUsuario(TipoUsuario.ADMIN);
        var entrenadores = repo.findByTipoUsuario(TipoUsuario.ENTRENADOR);
        var clientes = repo.findByTipoUsuario(TipoUsuario.CLIENTE);

        assertThat(admins).extracting(Usuario::getEmail).contains("admin@gym.test");
        assertThat(entrenadores).extracting(Usuario::getEmail)
                .contains("sofia@gym.test", "david@gym.test");
        assertThat(clientes).extracting(Usuario::getEmail)
                .contains("mario@gym.test");
    }

    /* =======================
       CRUD SEGURO (CREATE/DELETE)
       ======================= */

    @Test
    @DisplayName("save + deleteByIdUsuario: CRUD de usuario temporal sin relaciones")
    void create_delete_ok() {
        // CREATE
        Usuario temp = new Usuario();
        temp.setNombre("Test");
        temp.setApellidos("Temporal");
        temp.setEmail("temporal@gym.test");
        temp.setContrasenia("pwd");
        temp.setTipoUsuario(TipoUsuario.CLIENTE);
        temp.setTelefono("999999999");

        temp = repo.save(temp);
        assertNotNull(temp.getIdUsuario());

        // READ
        Usuario found = repo.findByEmail("temporal@gym.test").orElseThrow();
        assertEquals("Test", found.getNombre());

        // DELETE
        repo.deleteByIdUsuario(temp.getIdUsuario());
        repo.flush();

        assertThat(repo.findByIdUsuario(temp.getIdUsuario())).isEmpty();
        assertFalse(repo.existsByEmail("temporal@gym.test"));
    }
}
