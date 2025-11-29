package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.Cliente;
import es.unex.mdai.FitReserve.data.model.Reserva;
import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import es.unex.mdai.FitReserve.data.enume.Genero;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import es.unex.mdai.FitReserve.data.model.*;
import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.services.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private UsuarioServicio usuarioService;

    @Autowired
    private ClienteServicio clienteService;

    @Autowired
    private ReservaServicio reservaService;

    @Autowired
    private SalaServicio salaService;

    @Autowired
    private ActividadServicio actividadService;

    @Autowired
    private MaquinariaServicio maquinariaService;

    @Autowired
    private EntrenadorServicio entrenadorService;

    @PostMapping("/eliminar-cuenta")
    public String eliminarCuenta(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");

        if (usuario != null) {
            boolean ok = usuarioService.eliminarUsuario(usuario.getIdUsuario());
            session.invalidate();
            return "redirect:/";
        }

        return "redirect:/login";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");

        if (usuario == null) {
            return "redirect:/login";
        }

        Cliente cliente = clienteService.obtenerPorIdUsuario(usuario.getIdUsuario());
        model.addAttribute("usuario", usuario);
        model.addAttribute("cliente", cliente);

        return "perfilCliente";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String fechaNacimiento,
            @RequestParam String genero,
            @RequestParam(required = false) String objetivos,
            @RequestParam(required = false) String contrasenia,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            // Preparar y actualizar datos de Usuario -> recibimos el Usuario actualizado
            Usuario datosUsuario = new Usuario();
            datosUsuario.setNombre(nombre);
            datosUsuario.setApellidos(apellidos);
            datosUsuario.setEmail(email);
            datosUsuario.setTelefono(telefono);
            if (contrasenia != null && !contrasenia.isBlank()) {
                datosUsuario.setContrasenia(contrasenia);
            }

            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario.getIdUsuario(), datosUsuario);

            // Preparar y actualizar datos de Cliente -> recibimos booleano de éxito
            Cliente datosCliente = new Cliente();
            datosCliente.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            datosCliente.setGenero(Genero.valueOf(genero));
            datosCliente.setObjetivos(objetivos);

            boolean clienteOk = clienteService.actualizarCliente(usuario.getIdUsuario(), datosCliente);
            if (!clienteOk) {
                redirectAttributes.addFlashAttribute("error", "No se pudo actualizar los datos del cliente.");
                // Actualizar la sesión con lo que tengamos del usuario actualizado (si procede)
                session.setAttribute("usuarioSesion", usuarioActualizado);
                return "redirect:/cliente/perfil";
            }

            // Actualizar sesión con el usuario devuelto por el servicio
            session.setAttribute("usuarioSesion", usuarioActualizado);
            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil");
        }

        return "redirect:/cliente/perfil";
    }

    @GetMapping("/historial")
    public String mostrarHistorial(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) {
            return "redirect:/login";
        }

        Cliente cliente = clienteService.obtenerPorIdUsuario(usuario.getIdUsuario());
        if (cliente == null) {
            model.addAttribute("error", "No se encontró el cliente asociado al usuario.");
            return "clientePage";
        }

        List<Reserva> historial = reservaService.listarHistorialCliente(cliente.getIdCliente());

        List<Reserva> completadas = historial.stream()
                .filter(r -> r.getEstado() == Estado.Completada)
                .collect(Collectors.toList());

        List<Reserva> canceladas = historial.stream()
                .filter(r -> r.getEstado() == Estado.Cancelada)
                .collect(Collectors.toList());

        model.addAttribute("historialCompletadas", completadas);
        model.addAttribute("historialCanceladas", canceladas);
        model.addAttribute("usuario", usuario);

        return "historialReservaCliente";
    }

    @GetMapping("/nueva-reserva")
    public String mostrarFormularioReserva(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) {
            return "redirect:/login";
        }

        Cliente cliente = clienteService.obtenerPorIdUsuario(usuario.getIdUsuario());
        if (cliente == null) {
            model.addAttribute("error", "No se encontró el cliente asociado al usuario.");
            return "clientePage";
        }

        // Obtener datos necesarios para el formulario
        List<Sala> salas = salaService.listarTodas();
        List<Actividad> actividades = actividadService.listarTodas();
        List<Entrenador> entrenadores = entrenadorService.listarTodos();
        List<Maquinaria> maquinarias = maquinariaService.listarTodas();

        model.addAttribute("salas", salas);
        model.addAttribute("actividades", actividades);
        model.addAttribute("entrenadores", entrenadores);
        model.addAttribute("maquinarias", maquinarias);
        model.addAttribute("usuario", usuario);
        model.addAttribute("cliente", cliente);

        return "nuevaReserva";
    }

    @PostMapping("/nueva-reserva")
    public String crearReserva(
            @RequestParam Long idSala,
            @RequestParam Long idActividad,
            @RequestParam Long idEntrenador,
            @RequestParam String fechaHoraInicio,
            @RequestParam String fechaHoraFin,
            @RequestParam(required = false) String comentarios,
            @RequestParam(required = false) List<Long> maquinarias,
            @RequestParam(required = false) List<Integer> cantidades,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            Cliente cliente = clienteService.obtenerPorIdUsuario(usuario.getIdUsuario());
            Sala sala = salaService.obtenerSalaPorId(idSala);
            Actividad actividad = actividadService.obtenerActividadPorId(idActividad);
            Entrenador entrenador = entrenadorService.obtenerEntrenadorPorId(idEntrenador);

            Reserva reserva = new Reserva();
            reserva.setCliente(cliente);
            reserva.setSala(sala);
            reserva.setActividad(actividad);
            reserva.setEntrenador(entrenador);
            reserva.setFechaHoraInicio(LocalDateTime.parse(fechaHoraInicio));
            reserva.setFechaHoraFin(LocalDateTime.parse(fechaHoraFin));
            reserva.setComentarios(comentarios);
            reserva.setEstado(Estado.Pendiente);

            // Añadir maquinaria si se seleccionó
            if (maquinarias != null && cantidades != null) {
                List<ReservaMaquinaria> reservaMaquinarias = new ArrayList<>();
                for (int i = 0; i < maquinarias.size(); i++) {
                    Maquinaria maq = maquinariaService.obtenerMaquinariaPorId(maquinarias.get(i));
                    ReservaMaquinaria rm = new ReservaMaquinaria(reserva, maq, cantidades.get(i));
                    reservaMaquinarias.add(rm);
                }
                reserva.setMaquinariaAsignada(reservaMaquinarias);
            }

            boolean creada = reservaService.crearReserva(reserva);

            if (creada) {
                redirectAttributes.addFlashAttribute("mensaje", "Reserva creada exitosamente");
                return "redirect:/cliente";
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo crear la reserva. Verifica disponibilidad o la validez de lo campos.");
                return "redirect:/cliente/nueva-reserva";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la reserva");
            return "redirect:/cliente/nueva-reserva";
        }
    }

    @GetMapping("/mis-reservas")
    public String mostrarMisReservas(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) {
            return "redirect:/login";
        }

        Cliente cliente = clienteService.obtenerPorIdUsuario(usuario.getIdUsuario());
        if (cliente == null) {
            model.addAttribute("error", "No se encontró el cliente asociado al usuario.");
            return "clientePage";
        }

        // Obtener todas las reservas del cliente
        List<Reserva> todasReservas = reservaService.listarHistorialCliente(cliente.getIdCliente());

        // Filtrar solo reservas activas (Pendiente) que aún no han comenzado
        LocalDateTime ahora = LocalDateTime.now();
        List<Reserva> reservasActivas = todasReservas.stream()
                .filter(r -> r.getEstado() == Estado.Pendiente
                        && r.getFechaHoraInicio().isAfter(ahora))
                .collect(Collectors.toList());

        // Filtrar reservas de los próximos 7 días
        LocalDateTime dentroDe7Dias = ahora.plusDays(7);
        List<Reserva> reservasProximas = reservasActivas.stream()
                .filter(r -> r.getFechaHoraInicio().isBefore(dentroDe7Dias))
                .collect(Collectors.toList());

        model.addAttribute("reservasActivas", reservasActivas);
        model.addAttribute("reservasProximas", reservasProximas);
        model.addAttribute("usuario", usuario);

        return "misReservas";
    }

    @GetMapping("/reserva/{id}")
    public String verDetalleReserva(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) {
            return "redirect:/login";
        }

        Cliente cliente = clienteService.obtenerPorIdUsuario(usuario.getIdUsuario());
        Reserva reserva = reservaService.obtenerPorId(id);

        // Verificar que la reserva pertenece al cliente
        if (reserva == null || !reserva.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
            model.addAttribute("error", "Reserva no encontrada o no autorizada.");
            return "redirect:/cliente/mis-reservas";
        }

        model.addAttribute("reserva", reserva);
        model.addAttribute("usuario", usuario);

        return "detalleReserva";
    }

    @GetMapping("/reserva/editar/{id}")
    public String editarReserva(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) {
            return "redirect:/login";
        }

        Cliente cliente = clienteService.obtenerPorIdUsuario(usuario.getIdUsuario());
        Reserva reserva = reservaService.obtenerPorId(id);

        // Verificar que la reserva pertenece al cliente
        if (reserva == null || !reserva.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
            model.addAttribute("error", "Reserva no encontrada o no autorizada.");
            return "redirect:/cliente/mis-reservas";
        }

        // Cargar datos para el formulario
        List<Sala> salas = salaService.listarTodas();
        List<Actividad> actividades = actividadService.listarTodas();
        List<Entrenador> entrenadores = entrenadorService.listarTodos();
        List<Maquinaria> maquinarias = maquinariaService.listarTodas();

        model.addAttribute("reserva", reserva);
        model.addAttribute("salas", salas);
        model.addAttribute("actividades", actividades);
        model.addAttribute("entrenadores", entrenadores);
        model.addAttribute("maquinarias", maquinarias);
        model.addAttribute("usuario", usuario);

        return "editarReserva";
    }

    @PostMapping("/reserva/cancelar/{id}")
    public String cancelarReserva(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            Cliente cliente = clienteService.obtenerPorIdUsuario(usuario.getIdUsuario());

            // Usar el método específico de cancelación por cliente
            boolean cancelada = reservaService.cancelarPorCliente(id, cliente.getIdCliente());

            if (cancelada) {
                redirectAttributes.addFlashAttribute("mensaje", "Reserva cancelada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo cancelar la reserva. Verifica que no haya comenzado y pertenezca a tu cuenta.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar la reserva");
        }

        return "redirect:/cliente/mis-reservas";
    }

    @PostMapping("/reserva/editar/{id}")
    public String actualizarReserva(
            @PathVariable Long id,
            @RequestParam(required = false) Long idSala,
            @RequestParam(required = false) Long idActividad,
            @RequestParam(required = false) Long idEntrenador,
            @RequestParam(required = false) String fechaHoraInicio,
            @RequestParam(required = false) String fechaHoraFin,
            @RequestParam(required = false) String comentarios,
            @RequestParam(required = false) List<Long> maquinarias,
            @RequestParam(required = false) List<Integer> cantidades,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            Cliente cliente = clienteService.obtenerPorIdUsuario(usuario.getIdUsuario());
            Reserva reservaExistente = reservaService.obtenerPorId(id);

            // Verificar propiedad
            if (reservaExistente == null || !reservaExistente.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
                redirectAttributes.addFlashAttribute("error", "Reserva no encontrada o no autorizada.");
                return "redirect:/cliente/mis-reservas";
            }

            // Crear objeto con datos actualizados
            Reserva datosActualizados = new Reserva();

            if (idSala != null) {
                Sala sala = salaService.obtenerSalaPorId(idSala);
                datosActualizados.setSala(sala);
            }

            if (idActividad != null) {
                Actividad actividad = actividadService.obtenerActividadPorId(idActividad);
                datosActualizados.setActividad(actividad);
            }

            if (idEntrenador != null) {
                Entrenador entrenador = entrenadorService.obtenerEntrenadorPorId(idEntrenador);
                datosActualizados.setEntrenador(entrenador);
            }

            if (fechaHoraInicio != null && !fechaHoraInicio.isBlank()) {
                datosActualizados.setFechaHoraInicio(LocalDateTime.parse(fechaHoraInicio));
            }

            if (fechaHoraFin != null && !fechaHoraFin.isBlank()) {
                datosActualizados.setFechaHoraFin(LocalDateTime.parse(fechaHoraFin));
            }

            if (comentarios != null) {
                datosActualizados.setComentarios(comentarios);
            }

            // Maquinaria
            if (maquinarias != null && cantidades != null && !maquinarias.isEmpty()) {
                List<ReservaMaquinaria> reservaMaquinarias = new ArrayList<>();
                for (int i = 0; i < maquinarias.size(); i++) {
                    Maquinaria maq = maquinariaService.obtenerMaquinariaPorId(maquinarias.get(i));
                    if (maq != null) {
                        ReservaMaquinaria rm = new ReservaMaquinaria(reservaExistente, maq, cantidades.get(i));
                        reservaMaquinarias.add(rm);
                    }
                }
                datosActualizados.setMaquinariaAsignada(reservaMaquinarias);
            }

            boolean actualizada = reservaService.actualizarReserva(id, datosActualizados);

            if (actualizada) {
                redirectAttributes.addFlashAttribute("mensaje", "Reserva actualizada correctamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo actualizar la reserva. Verifica disponibilidad.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la reserva ");
        }

        return "redirect:/cliente/mis-reservas";
    }

}
