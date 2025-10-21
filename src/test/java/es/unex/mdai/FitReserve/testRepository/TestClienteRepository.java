package es.unex.mdai.FitReserve.testRepository;

import es.unex.mdai.FitReserve.data.model.Cliente;
import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.data.repository.ClienteRepository;
import es.unex.mdai.FitReserve.data.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Test
    @DisplayName("findAll() devuelve los clientes existentes")
    void findAll_ok() {
        List<Cliente> all = clienteRepo.findAll();
        assertThat(all).isNotEmpty();

        // (Opcional) comprobamos que existen los dos del seed
        Long adminId = usuarioRepo.findByEmail("admin@gym.test").map(Usuario::getIdUsuario).orElse(null);
        Long marioId = usuarioRepo.findByEmail("mario@gym.test").map(Usuario::getIdUsuario).orElse(null);

        assertThat(all).extracting(Cliente::getIdCliente)
                .contains(adminId, marioId);
    }

    @Test
    @DisplayName("findByIdCliente: presente y ausente")
    void findByIdCliente_present_absent() {
        Long adminId = usuarioRepo.findByEmail("admin@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        assertThat(clienteRepo.findByIdCliente(adminId)).isPresent();
        assertThat(clienteRepo.findByIdCliente(9_999_999L)).isNotPresent();
    }

    @Test
    @DisplayName("findByUsuarioIdUsuario devuelve el cliente asociado al usuario")
    void findByUsuarioIdUsuario_ok() {
        Long marioId = usuarioRepo.findByEmail("mario@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        var cli = clienteRepo.findByUsuarioIdUsuario(marioId);
        assertThat(cli).isPresent();
        assertThat(cli.get().getIdCliente()).isEqualTo(marioId); // PK=FK
    }

    @Test
    @DisplayName("save() actualiza campos del cliente (p.ej., objetivos)")
    void update_objetivos_ok() {
        Long marioId = usuarioRepo.findByEmail("mario@gym.test").map(Usuario::getIdUsuario).orElseThrow();

        Cliente c = clienteRepo.findByIdCliente(marioId).orElseThrow();
        String prev = c.getObjetivos();

        c.setObjetivos("Mejorar fuerza y movilidad");
        clienteRepo.save(c);

        Cliente updated = clienteRepo.findByIdCliente(marioId).orElseThrow();
        assertThat(updated.getObjetivos()).isEqualTo("Mejorar fuerza y movilidad");
        assertThat(updated.getObjetivos()).isNotEqualTo(prev);
    }
}
