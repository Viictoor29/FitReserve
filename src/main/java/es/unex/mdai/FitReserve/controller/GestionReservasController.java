package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.*;
import es.unex.mdai.FitReserve.services.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/reservas")
public class GestionReservasController {

    private final ReservaServicio reservaServicio;
    private final ClienteServicio clienteServicio;
    private final EntrenadorServicio entrenadorServicio;
    private final ActividadServicio actividadServicio;
    private final SalaServicio salaServicio;

    public GestionReservasController(ReservaServicio reservaServicio,
                                     ClienteServicio clienteServicio,
                                     EntrenadorServicio entrenadorServicio,
                                     ActividadServicio actividadServicio,
                                     SalaServicio salaServicio) {
        this.reservaServicio = reservaServicio;
        this.clienteServicio = clienteServicio;
        this.entrenadorServicio = entrenadorServicio;
        this.actividadServicio = actividadServicio;
        this.salaServicio = salaServicio;
    }

    /* ========== LISTADO PRINCIPAL ========== */

    @GetMapping
    public String listarReservas(Model model) {
        List<Reserva> reservas = reservaServicio.listarTodas();
        model.addAttribute("reservas", reservas);
        return "gestionReservasPage";
    }

    /* ========== VER DETALLE ========== */

    @GetMapping("/{id}")
    public String verReserva(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Reserva reserva = reservaServicio.obtenerPorId(id);
        if (reserva == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "La reserva no existe.");
            return "redirect:/admin/reservas";
        }
        model.addAttribute("reserva", reserva);
        return "verReservaAdmin";
    }

    /* ========== NUEVA RESERVA ========== */

    @GetMapping("/nueva")
    public String mostrarNuevaReserva(Model model) {
        model.addAttribute("clientes", clienteServicio.listarTodos());
        model.addAttribute("entrenadores", entrenadorServicio.listarTodos());
        model.addAttribute("actividades", actividadServicio.listarTodas());
        model.addAttribute("salas", salaServicio.listarTodas());
        model.addAttribute("estados", Estado.values()); // por si quieres permitir cambiar estado en admin
        return "nuevaReservaAdmin";
    }

    @PostMapping("/nueva")
    public String procesarNuevaReserva(
            @RequestParam("inicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,

            @RequestParam("fin")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,

            @RequestParam("clienteId") Long clienteId,
            @RequestParam("entrenadorId") Long entrenadorId,
            @RequestParam("actividadId") Long actividadId,
            @RequestParam("salaId") Long salaId,
            @RequestParam(name = "comentarios", required = false) String comentarios,
            RedirectAttributes redirectAttributes,
            Model model) {

        Cliente cliente = clienteServicio.obtenerClientePorId(clienteId);
        Entrenador entrenador = entrenadorServicio.obtenerEntrenadorPorId(entrenadorId);
        Actividad actividad = actividadServicio.obtenerActividadPorId(actividadId);
        Sala sala = salaServicio.obtenerSalaPorId(salaId);

        Reserva reserva = new Reserva();
        reserva.setFechaHoraInicio(inicio);
        reserva.setFechaHoraFin(fin);
        reserva.setCliente(cliente);
        reserva.setEntrenador(entrenador);
        reserva.setActividad(actividad);
        reserva.setSala(sala);
        reserva.setComentarios(comentarios);

        boolean ok = reservaServicio.crearReserva(reserva);

        if (!ok) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se ha podido crear la reserva. Revisa horarios y disponibilidad.");
            return "redirect:/admin/reservas";
        }

        redirectAttributes.addFlashAttribute("mensajeExito", "Reserva creada correctamente.");
        return "redirect:/admin/reservas";
    }

    /* ========== EDITAR RESERVA ========== */

    @GetMapping("/editar/{id}")
    public String mostrarEditarReserva(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Reserva reserva = reservaServicio.obtenerPorId(id);
        if (reserva == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "La reserva no existe.");
            return "redirect:/admin/reservas";
        }

        model.addAttribute("reserva", reserva);
        model.addAttribute("clientes", clienteServicio.listarTodos());
        model.addAttribute("entrenadores", entrenadorServicio.listarTodos());
        model.addAttribute("actividades", actividadServicio.listarTodas());
        model.addAttribute("salas", salaServicio.listarTodas());
        model.addAttribute("estados", Estado.values());

        return "editarReservaAdmin";
    }

    @PostMapping("/editar/{id}")
    public String procesarEditarReserva(
            @PathVariable Long id,
            @RequestParam("inicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,

            @RequestParam("fin")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,

            @RequestParam("clienteId") Long clienteId,
            @RequestParam("entrenadorId") Long entrenadorId,
            @RequestParam("actividadId") Long actividadId,
            @RequestParam("salaId") Long salaId,
            @RequestParam("estado") Estado estado,
            @RequestParam(name = "comentarios", required = false) String comentarios,
            RedirectAttributes redirectAttributes) {

        Cliente cliente = clienteServicio.obtenerClientePorId(clienteId);
        Entrenador entrenador = entrenadorServicio.obtenerEntrenadorPorId(entrenadorId);
        Actividad actividad = actividadServicio.obtenerActividadPorId(actividadId);
        Sala sala = salaServicio.obtenerSalaPorId(salaId);

        Reserva datos = new Reserva();
        datos.setFechaHoraInicio(inicio);
        datos.setFechaHoraFin(fin);
        datos.setCliente(cliente);
        datos.setEntrenador(entrenador);
        datos.setActividad(actividad);
        datos.setSala(sala);
        datos.setComentarios(comentarios);
        datos.setEstado(estado); // aunque en tu servicio ahora mismo no se usa mucho

        boolean ok = reservaServicio.actualizarReserva(id, datos);

        if (!ok) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se ha podido actualizar la reserva. Puede que ya haya empezado o que no exista.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito", "Reserva actualizada correctamente.");
        }

        return "redirect:/admin/reservas";
    }

    /* ========== ELIMINAR RESERVA ========== */

    @PostMapping("/eliminar/{id}")
    public String eliminarReserva(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        boolean ok = reservaServicio.eliminarReserva(id);

        if (ok) {
            redirectAttributes.addFlashAttribute("mensajeExito", "Reserva eliminada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "No se ha podido eliminar la reserva.");
        }

        return "redirect:/admin/reservas";
    }
}
