package es.unex.mdai.FitReserve.testRepository;

import es.unex.mdai.FitReserve.data.enume.*;
import es.unex.mdai.FitReserve.data.model.*;
import es.unex.mdai.FitReserve.data.repository.*;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FitReserveIntegrationTest {

    @Autowired
    private ClienteRepository repoCliente;

    @Autowired
    private EntrenadorRepository repoEntrnador;

    @Autowired
    private UsuarioRepository repoUsuario;

    @Autowired
    private ActividadRepository repoActividad;

    @Autowired
    private ReservaMaquinariaRepository repoReservaMaquinaria;

    @Autowired
    private MaquinariaRepository repoMaquinaria;

    @Autowired
    private ReservaRepository repoReserva;

    @Autowired
    private SalaRepository repoSala;

    @Test
    void testRepositoriesNotNull() {
        assertThat(repoCliente).isNotNull();
        assertThat(repoEntrnador).isNotNull();
        assertThat(repoUsuario).isNotNull();
        assertThat(repoActividad).isNotNull();
        assertThat(repoReservaMaquinaria).isNotNull();
        assertThat(repoMaquinaria).isNotNull();
        assertThat(repoReserva).isNotNull();
        assertThat(repoSala).isNotNull();
    }

    @Test
    @Order(1)
    @Commit
    void testCrearUsuario_IniciarSesion() {
        // ==== ADMIN ============================================================
        Usuario admin = new Usuario(
                "Admin", "Root", "admin@test.com", "admin123",
                TipoUsuario.ADMIN, "600000001"
        );
        admin = repoUsuario.save(admin);
        assertThat(admin.getIdUsuario()).isNotNull();

        // ==== ENTRENADOR (Usuario + Entrenador con @MapsId) ====================
        Usuario uEntr = new Usuario(
                "Enrique", "Trainer", "entrenador@test.com", "pass1234",
                TipoUsuario.ENTRENADOR, "600000002"
        );
        uEntr = repoUsuario.save(uEntr);

        Entrenador entrenador = new Entrenador(
                "Fuerza",                  // especialidad (no null)
                5,                         // experiencia 1..10
                java.time.LocalTime.of(8, 0),   // horaInicioTrabajo (no null)
                java.time.LocalTime.of(16, 0),  // horaFinTrabajo (no null)
                uEntr                      // @OneToOne + @MapsId
        );
        entrenador = repoEntrnador.save(entrenador);
        assertThat(entrenador.getIdEntrenador()).isEqualTo(uEntr.getIdUsuario());

        // ==== CLIENTE (Usuario + Cliente con @MapsId) ==========================
        Usuario uCli = new Usuario(
                "Clara", "Client", "cliente@test.com", "pass1234",
                TipoUsuario.CLIENTE, "600000003"
        );
        uCli = repoUsuario.save(uCli);

        Cliente cliente = new Cliente(
                java.time.LocalDate.of(1995, 1, 15), // fechaNacimiento (no null)
                Genero.FEMENINO,                      // ajusta al enum real si difiere
                "Perder peso",                        // objetivos (opcional)
                uCli                                  // @OneToOne + @MapsId
        );
        cliente = repoCliente.save(cliente);
        assertThat(cliente.getIdCliente()).isEqualTo(uCli.getIdUsuario());

        // ==== INICIAR SESION ==================================================

        // === LOGIN ADMIN ===
        Optional<Usuario> adminOpt = repoUsuario.findByEmailAndContrasenia("admin@test.com", "admin123");
        assertThat(adminOpt)
                .as("Admin debería poder iniciar sesión con sus credenciales")
                .isPresent();

        Usuario adminL = adminOpt.get();
        assertThat(adminL.getTipoUsuario().name()).isEqualTo("ADMIN");

        // === LOGIN ENTRENADOR ===
        Optional<Usuario> entrenOpt = repoUsuario.findByEmailAndContrasenia("entrenador@test.com", "pass1234");
        assertThat(entrenOpt)
                .as("Entrenador debería poder iniciar sesión con sus credenciales")
                .isPresent();

        Usuario entrenadorL = entrenOpt.get();
        assertThat(entrenadorL.getTipoUsuario().name()).isEqualTo("ENTRENADOR");

        // === LOGIN CLIENTE ===
        Optional<Usuario> clienteOpt = repoUsuario.findByEmailAndContrasenia("cliente@test.com", "pass1234");
        assertThat(clienteOpt)
                .as("Cliente debería poder iniciar sesión con sus credenciales")
                .isPresent();

        Usuario clienteL = clienteOpt.get();
        assertThat(clienteL.getTipoUsuario().name()).isEqualTo("CLIENTE");

        // === LOGIN FALLIDO ===
        Optional<Usuario> invalido = repoUsuario.findByEmailAndContrasenia("admin@test.com", "contramal");
        assertThat(invalido)
                .as("Debe fallar con contraseña incorrecta")
                .isNotPresent();
    }

    @Test
    @Order(2)
    @Commit
    void anadirActividad_Sala_Maquinaria () {

        // ==== ACTIVIDAD ========================================================
        Actividad act = new Actividad(
                "Act1",
                "Actividad de prueba",
                TipoActividad.values()[0],
                NivelActividad.values()[0]
        );
        act = repoActividad.save(act);
        assertThat(act.getIdActividad()).isNotNull();
        assertThat(repoActividad.findById(act.getIdActividad())).isPresent();

        // ==== SALA =======================================================
        Sala sala = new Sala(
                "Sala1",
                20,
                "Primera planta"
        );
        sala= repoSala.save(sala);
        assertThat(sala.getIdSala()).isNotNull();
        assertThat(repoSala.findById(sala.getIdSala())).isPresent();

        // ==== MAQUINARIA =======================================================
        Maquinaria ma = new Maquinaria(
                "Maquina1",
                10,
                TipoActividad.values()[0],
                "Maquinaria de prueba"
        );
        ma= repoMaquinaria.save(ma);
        assertThat(ma.getIdMaquinaria()).isNotNull();
        assertThat(repoMaquinaria.findById(ma.getIdMaquinaria())).isPresent();
    }

    @Test
    @Order(3)
    @Commit
    void hacerReserva_ReservaMaquinaria() {

        // ==== RESERVA ==========================================================
        Reserva reserva = new Reserva(
                LocalDateTime.of(2025, 10, 22, 14, 30),
                LocalDateTime.of(2025, 10, 22, 15, 30),
                Estado.Pendiente,                   // usa los nombres de tu enum
                "Reserva de prueba"
        );

        // Cliente
        Usuario uCliente = repoUsuario.findByEmail("cliente@test.com")
                .orElseThrow(() -> new IllegalStateException("Falta usuario cliente@test.com"));
        Cliente cliente = repoCliente.findById(uCliente.getIdUsuario())
                .orElseThrow(() -> new IllegalStateException("Falta Cliente con id " + uCliente.getIdUsuario()));
        reserva.setCliente(cliente);

        // Entrenador (ojo al typo en el email)
        Usuario uEntrenador = repoUsuario.findByEmail("entrenador@test.com")
                .orElseThrow(() -> new IllegalStateException("Falta usuario entrenador@test.com"));
        Entrenador entrenador = repoEntrnador.findById(uEntrenador.getIdUsuario())
                .orElseThrow(() -> new IllegalStateException("Falta Entrenador con id " + uEntrenador.getIdUsuario()));
        reserva.setEntrenador(entrenador);

        // Actividad
        Actividad actividad = repoActividad.findByNombre("Act1")
                .orElseThrow(() -> new IllegalStateException("Falta Actividad 'Act1'"));
        reserva.setActividad(actividad);

        // Sala
        Sala sala = repoSala.findByNombre("Sala1")
                .orElseThrow(() -> new IllegalStateException("Falta Sala 'Sala1'"));
        reserva.setSala(sala);

        // Guarda la reserva
        reserva = repoReserva.save(reserva);
        assertThat(reserva.getIdReserva()).isNotNull();

        // ==== DISPONIBILIDAD DE MAQUINARIA =====================================
        // Estados que consideras "activos" (añade CONFIRMADA si existe en tu enum)
        var estadosActivos = List.of(Estado.Pendiente);

        // Consulta máquinas disponibles en el intervalo de la reserva
        List<Maquinaria> disponibles = repoMaquinaria.findDisponibles(
                reserva.getFechaHoraInicio(),
                reserva.getFechaHoraFin(),
                estadosActivos
        );
        assertThat(disponibles).as("Debe haber alguna maquinaria disponible").isNotEmpty();

        // ==== ASIGNAR MAQUINARIA A LA RESERVA ==================================
        Maquinaria maqSolicitada = repoMaquinaria.findByNombre("Maquina1")
                .orElseThrow(() -> new IllegalStateException("Falta Maquinaria 'Maquina1'"));

        assertThat(disponibles)
                .as("Maquina1 debe estar disponible para la franja solicitada")
                .anyMatch(m -> m.getIdMaquinaria().equals(maqSolicitada.getIdMaquinaria()));

        int cantidadSolicitada = 2;

        // IMPORTANTE: tu query garantiza >0 libres, no que haya >= cantidadSolicitada.
        // Si te basta con eso, asigna. Si necesitas garantizar stock >= N, amplía la query como te comenté antes.
        assertThat(cantidadSolicitada)
                .as("Cantidad solicitada debe ser positiva")
                .isGreaterThan(0);

        // Crea el vínculo Reserva-Maquinaria
        ReservaMaquinaria rm = new ReservaMaquinaria(reserva, maqSolicitada, cantidadSolicitada);
        rm = repoReservaMaquinaria.save(rm);

        // Verificaciones
        assertThat(reserva).isNotNull();
        assertThat(reserva.getIdReserva()).isNotNull();
        assertThat(rm.getIdReserva()).isEqualTo(reserva.getIdReserva());
        assertThat(rm.getIdMaquinaria()).isEqualTo(maqSolicitada.getIdMaquinaria());
        assertThat(rm.getCantidad()).isEqualTo(cantidadSolicitada);
    }
}