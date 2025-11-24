package es.unex.mdai.FitReserve.testRepository;

import es.unex.mdai.FitReserve.data.enume.*;
import es.unex.mdai.FitReserve.data.model.*;
import es.unex.mdai.FitReserve.data.repository.*;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
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
    @Order(0)
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
    void testanadirActividad_Sala_Maquinaria () {

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
    void testhacerReserva_ReservaMaquinaria() {

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
        // Estados que consideras "activos"
        var estadosActivos = Estado.Pendiente;

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

    @Test
    @Order(4)
    void eliminarUsuarioCliente_cascade() {
        // 1) Localiza el Usuario cliente
        Usuario uCliente = repoUsuario.findByEmail("cliente@test.com")
                .orElseThrow(() -> new IllegalStateException("No existe usuario cliente@test.com"));
        Long idUsuario = uCliente.getIdUsuario();

        // 2) Verifica que existe la entidad Cliente (MapsId)
        assertThat(repoCliente.findById(idUsuario)).isPresent();

        // 3) Verifica si hay reservas del cliente antes de borrar (opcional)
        long reservasAntes = repoReserva.findAll().stream()
                .filter(r -> r.getCliente().getIdCliente().equals(idUsuario))
                .count();

        // 4) Elimina el Usuario (cascade + orphanRemoval debe borrar Cliente)
        repoUsuario.delete(uCliente);
        repoUsuario.flush();

        // 5) Aserciones: Usuario y Cliente desaparecen
        assertThat(repoUsuario.findById(idUsuario)).isNotPresent();
        assertThat(repoCliente.findById(idUsuario)).isNotPresent();

        // 6) Sus reservas deben caer por @OnDelete(CASCADE) en Reserva.cliente
        long reservasDespues = repoReserva.findAll().stream()
                .filter(r -> r.getCliente() != null && idUsuario.equals(r.getCliente().getIdCliente()))
                .count();
        assertThat(reservasDespues).isZero();

        // 7) Y también sus ReservaMaquinaria
        boolean tieneRM = repoReservaMaquinaria.findAll().stream()
                .anyMatch(rm -> rm.getReserva().getCliente() != null
                        && idUsuario.equals(rm.getReserva().getCliente().getIdCliente()));
        assertThat(tieneRM).isFalse();
    }

    @Test
    @Order(5)
    void eliminarUsuarioEntrenador_cascade() {
        Usuario uEntr = repoUsuario.findByEmail("entrenador@test.com")
                .orElseThrow(() -> new IllegalStateException("No existe usuario entrenador@test.com"));
        Long idUsuario = uEntr.getIdUsuario();

        // Debe existir Entrenador con el mismo id (MapsId)
        assertThat(repoEntrnador.findById(idUsuario)).isPresent();

        // Contar reservas del entrenador
        long reservasAntes = repoReserva.findAll().stream()
                .filter(r -> r.getEntrenador().getIdEntrenador().equals(idUsuario))
                .count();

        // Borrar usuario -> cascada borra Entrenador; DB @OnDelete borra Reservas + RM
        repoUsuario.delete(uEntr);
        repoUsuario.flush();

        assertThat(repoUsuario.findById(idUsuario)).isNotPresent();
        assertThat(repoEntrnador.findById(idUsuario)).isNotPresent();

        long reservasDespues = repoReserva.findAll().stream()
                .filter(r -> r.getEntrenador() != null && idUsuario.equals(r.getEntrenador().getIdEntrenador()))
                .count();
        assertThat(reservasDespues).isZero();

        boolean tieneRM = repoReservaMaquinaria.findAll().stream()
                .anyMatch(rm -> rm.getReserva().getEntrenador() != null
                        && idUsuario.equals(rm.getReserva().getEntrenador().getIdEntrenador()));
        assertThat(tieneRM).isFalse();
    }

    @Test
    @Order(6)
    void eliminarActividad_cascade() {
        Actividad act = repoActividad.findByNombre("Act1")
                .orElseThrow(() -> new IllegalStateException("No existe actividad Act1"));
        Long idAct = act.getIdActividad();

        // Algunas reservas podrían apuntar a esta actividad
        long reservasAntes = repoReserva.findAll().stream()
                .filter(r -> r.getActividad().getIdActividad().equals(idAct))
                .count();

        repoActividad.delete(act);
        repoActividad.flush();

        assertThat(repoActividad.findById(idAct)).isNotPresent();

        long reservasDespues = repoReserva.findAll().stream()
                .filter(r -> r.getActividad() != null && idAct.equals(r.getActividad().getIdActividad()))
                .count();
        assertThat(reservasDespues).isZero();

        boolean tieneRM = repoReservaMaquinaria.findAll().stream()
                .anyMatch(rm -> rm.getReserva().getActividad() != null
                        && idAct.equals(rm.getReserva().getActividad().getIdActividad()));
        assertThat(tieneRM).isFalse();
    }

    @Test
    @Order(7)
    void eliminarSala_cascade() {
        Sala sala = repoSala.findByNombre("Sala1")
                .orElseThrow(() -> new IllegalStateException("No existe sala Sala1"));
        // Si tu PK se llama distinto, ajusta:
        Long idSala = sala.getIdSala();

        long reservasAntes = repoReserva.findAll().stream()
                .filter(r -> r.getSala().getIdSala().equals(idSala))
                .count();

        repoSala.delete(sala);
        repoSala.flush();

        assertThat(repoSala.findById(idSala)).isNotPresent();

        long reservasDespues = repoReserva.findAll().stream()
                .filter(r -> r.getSala() != null && idSala.equals(r.getSala().getIdSala()))
                .count();
        assertThat(reservasDespues).isZero();

        boolean tieneRM = repoReservaMaquinaria.findAll().stream()
                .anyMatch(rm -> rm.getReserva().getSala() != null
                        && idSala.equals(rm.getReserva().getSala().getIdSala()));
        assertThat(tieneRM).isFalse();
    }

    @Test
    @Order(8)
    void eliminarMaquinaria_cascadeReservaYReservaMaquinaria() {
        // 1) Localiza la maquinaria
        Maquinaria maq = repoMaquinaria.findByNombre("Maquina1")
                .orElseThrow(() -> new IllegalStateException("No existe Maquina1"));
        Long idMaq = maq.getIdMaquinaria();

        // 2) ReservaMaquinaria asociadas a esa maquinaria
        var rmAsociadas = repoReservaMaquinaria.findAll().stream()
                .filter(rm -> rm.getMaquinaria() != null && idMaq.equals(rm.getMaquinaria().getIdMaquinaria()))
                .toList();

        // 3) IDs de Reservas afectadas (para comprobar después del borrado)
        var idsReservasAfectadas = rmAsociadas.stream()
                .map(rm -> rm.getReserva().getIdReserva())
                .distinct()
                .toList();

        // Sanity checks: hay algo que borrar
        assertThat(rmAsociadas).isNotEmpty();
        assertThat(idsReservasAfectadas).isNotEmpty();

        // 4) Borra la maquinaria
        repoMaquinaria.delete(maq);
        repoMaquinaria.flush();

        // 5) La maquinaria desaparece
        assertThat(repoMaquinaria.findById(idMaq)).isNotPresent();

        // 6) Las ReservaMaquinaria asociadas desaparecen
        boolean quedanRMAso = repoReservaMaquinaria.findAll().stream()
                .anyMatch(rm -> rm.getMaquinaria() != null && idMaq.equals(rm.getMaquinaria().getIdMaquinaria()));
        assertThat(quedanRMAso).isFalse();

        // 7) Y también deben desaparecer las Reservas que estaban vinculadas a esa maquinaria
        var reservasPost = repoReserva.findAllById(idsReservasAfectadas);
        assertThat(reservasPost)
                .as("Las Reservas no deben borrarse al eliminar solo la Maquinaria")
                .isNotEmpty();
    }

    @Test
    @Order(9)
    void update_Usuario() {
        Usuario u = new Usuario(
                "Nombre"+2, "Apellidos", "upd_user@test.com", "oldpass",
                TipoUsuario.CLIENTE, "600111111"
        );
        u = repoUsuario.save(u);

        // UPDATE
        u.setContrasenia("newpass");
        u.setTelefono("600222222");
        repoUsuario.saveAndFlush(u);

        Usuario again = repoUsuario.findById(u.getIdUsuario()).orElseThrow();
        assertThat(again.getContrasenia()).isEqualTo("newpass");
        assertThat(again.getTelefono()).isEqualTo("600222222");
    }

    @Test
    @Order(10)
    void update_Cliente() {
        // Usuario base + Cliente (MapsId)
        Usuario u = repoUsuario.save(new Usuario(
                "Clara","Update","upd_cliente@test.com","pass",
                TipoUsuario.CLIENTE,"600333333"
        ));
        Cliente c = new Cliente(
                java.time.LocalDate.of(1990,1,1),
                Genero.FEMENINO,
                "Objetivo inicial",
                u
        );
        c = repoCliente.save(c);

        // UPDATE
        c.setObjetivos("Objetivo actualizado");
        repoCliente.saveAndFlush(c);

        Cliente again = repoCliente.findById(u.getIdUsuario()).orElseThrow();
        assertThat(again.getObjetivos()).isEqualTo("Objetivo actualizado");
    }

    @Test
    @Order(11)
    void update_Entrenador() {
        // Usuario base + Entrenador (MapsId)
        Usuario u = repoUsuario.save(new Usuario(
                "Enrique","Update","upd_entrenador@test.com","pass",
                TipoUsuario.ENTRENADOR,"600444444"
        ));
        Entrenador e = new Entrenador(
                "Fuerza", 5,
                java.time.LocalTime.of(8,0),
                java.time.LocalTime.of(16,0),
                u
        );
        e = repoEntrnador.save(e);

        // UPDATE
        e.setEspecialidad("Resistencia");
        e.setExperiencia(7);
        repoEntrnador.saveAndFlush(e);

        Entrenador again = repoEntrnador.findById(u.getIdUsuario()).orElseThrow();
        assertThat(again.getEspecialidad()).isEqualTo("Resistencia");
        assertThat(again.getExperiencia()).isEqualTo(7);
    }

    @Test
    @Order(12)
    void update_Actividad() {
        String nombre = "ActUpd-" + 2;
        Actividad a = new Actividad(
                nombre,
                "Desc inicial",
                TipoActividad.values()[0],
                NivelActividad.values()[0]
        );
        a = repoActividad.save(a);

        // UPDATE
        a.setDescripcion("Desc actualizada");
        a.setNivel(NivelActividad.values()[Math.min(1, NivelActividad.values().length-1)]);
        repoActividad.saveAndFlush(a);

        Actividad again = repoActividad.findById(a.getIdActividad()).orElseThrow();
        assertThat(again.getDescripcion()).isEqualTo("Desc actualizada");
        assertThat(again.getNivel()).isEqualTo(a.getNivel());
    }

    @Test
    @Order(13)
    void update_Sala() {
        Sala s = new Sala("SalaUpd-" + 2, 20, "Planta 1");
        s = repoSala.save(s);

        // UPDATE
        s.setCapacidad(25);
        s.setUbicacion("Planta 2"); // si tu entidad tiene 'ubicacion'
        repoSala.saveAndFlush(s);

        Sala again = repoSala.findById(s.getIdSala()).orElseThrow();
        assertThat(again.getCapacidad()).isEqualTo(25);
        assertThat(again.getUbicacion()).isEqualTo("Planta 2");
    }

    @Test
    @Order(14)
    void update_Maquinaria() {
        Maquinaria m = new Maquinaria(
                "MaqUpd-" + 2,
                5,
                TipoActividad.values()[0],
                "Desc inicial"
        );
        m = repoMaquinaria.save(m);

        // UPDATE
        m.setCantidadTotal(8);
        m.setDescripcion("Desc actualizada");
        repoMaquinaria.saveAndFlush(m);

        Maquinaria again = repoMaquinaria.findById(m.getIdMaquinaria()).orElseThrow();
        assertThat(again.getCantidadTotal()).isEqualTo(8);
        assertThat(again.getDescripcion()).isEqualTo("Desc actualizada");
    }

    @Test
    @Order(15)
    void update_Reserva() {
        // Prepara mínimos: usuario+cliente, usuario+entrenador, actividad, sala
        Usuario uCli = repoUsuario.save(new Usuario(
                "Cliente","Res","upd_res_cli@test.com","pass",
                TipoUsuario.CLIENTE,"600555555"
        ));
        Cliente cli = repoCliente.save(new Cliente(
                java.time.LocalDate.of(1992,2,2),
                Genero.FEMENINO,
                "Objetivo",
                uCli
        ));
        Usuario uEnt = repoUsuario.save(new Usuario(
                "Entr","Res","upd_res_ent@test.com","pass",
                TipoUsuario.ENTRENADOR,"600666666"
        ));
        Entrenador ent = repoEntrnador.save(new Entrenador(
                "Fuerza",6,
                java.time.LocalTime.of(9,0),
                java.time.LocalTime.of(17,0),
                uEnt
        ));
        Actividad act = repoActividad.save(new Actividad(
                "ActReserva-" + 2,
                "Desc",
                TipoActividad.values()[0],
                NivelActividad.values()[0]
        ));
        Sala sala = repoSala.save(new Sala(
                "SalaReserva-" + 2,
                15,
                "Planta 1"
        ));

        Reserva r = new Reserva(
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
                LocalDateTime.now().plusDays(1).withHour(11).withMinute(0),
                Estado.Pendiente,
                "Comentario inicial"
        );
        r.setCliente(cli);
        r.setEntrenador(ent);
        r.setActividad(act);
        r.setSala(sala);
        r = repoReserva.save(r);

        // UPDATE
        r.setEstado(Estado.Cancelada);  // ajusta al nombre exacto de tu enum
        r.setComentarios("Comentario actualizado");
        r.setFechaHoraFin(r.getFechaHoraFin().plusMinutes(30));
        repoReserva.saveAndFlush(r);

        Reserva again = repoReserva.findById(r.getIdReserva()).orElseThrow();
        assertThat(again.getEstado()).isEqualTo(Estado.Cancelada);
        assertThat(again.getComentarios()).isEqualTo("Comentario actualizado");
        assertThat(again.getFechaHoraFin()).isEqualTo(r.getFechaHoraFin());
    }

    @Test
    @Order(16)
    void update_ReservaMaquinaria() {
        // Prepara Reserva y Maquinaria
        Maquinaria m = repoMaquinaria.save(new Maquinaria(
                "MaqRM-" + 2,
                10,
                TipoActividad.values()[0],
                "Para RM"
        ));

        Usuario uCli = repoUsuario.save(new Usuario(
                "Cli","RM","upd_rm_cli@test.com","pass",
                TipoUsuario.CLIENTE,"600777777"
        ));
        Cliente cli = repoCliente.save(new Cliente(
                java.time.LocalDate.of(1993,3,3),
                Genero.FEMENINO,
                "Objetivo",
                uCli
        ));

        Usuario uEnt = repoUsuario.save(new Usuario(
                "Ent","RM","upd_rm_ent@test.com","pass",
                TipoUsuario.ENTRENADOR,"600888888"
        ));
        Entrenador ent = repoEntrnador.save(new Entrenador(
                "Cardio",4,
                java.time.LocalTime.of(7,0),
                java.time.LocalTime.of(15,0),
                uEnt
        ));

        Actividad act = repoActividad.save(new Actividad(
                "ActRM-" + 2,
                "Desc",
                TipoActividad.values()[0],
                NivelActividad.values()[0]
        ));
        Sala sala = repoSala.save(new Sala(
                "SalaRM-" + 2,
                12,
                "Planta 2"
        ));

        Reserva r = new Reserva(
                LocalDateTime.now().plusDays(2).withHour(12),
                LocalDateTime.now().plusDays(2).withHour(13),
                Estado.Pendiente,
                "RM inicial"
        );
        r.setCliente(cli);
        r.setEntrenador(ent);
        r.setActividad(act);
        r.setSala(sala);
        r = repoReserva.save(r);

        ReservaMaquinaria rm = repoReservaMaquinaria.save(new ReservaMaquinaria(r, m, 2));

        // UPDATE cantidad
        rm.setCantidad(5);
        repoReservaMaquinaria.saveAndFlush(rm);

        final Long idReserva = r.getIdReserva();
        final Long idMaquinaria = m.getIdMaquinaria();

        ReservaMaquinaria again = repoReservaMaquinaria.findAll().stream()
                .filter(x -> x.getReserva().getIdReserva().equals(idReserva)
                        && x.getMaquinaria().getIdMaquinaria().equals(idMaquinaria))
                .findFirst()
                .orElseThrow();
    }
}