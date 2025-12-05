// src/main/java/es/unex/mdai/FitReserve/controller/GestionReservasController.java
package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.*;
import es.unex.mdai.FitReserve.services.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/reservas")
public class GestionReservasController {

    private final ReservaServicio reservaServicio;
    private final ClienteServicio clienteServicio;
    private final EntrenadorServicio entrenadorServicio;
    private final ActividadServicio actividadServicio;
    private final SalaServicio salaServicio;
    private final MaquinariaServicio maquinariaServicio;

    public GestionReservasController(ReservaServicio reservaServicio,
                                     ClienteServicio clienteServicio,
                                     EntrenadorServicio entrenadorServicio,
                                     ActividadServicio actividadServicio,
                                     SalaServicio salaServicio,
                                     MaquinariaServicio maquinariaServicio) {
        this.reservaServicio = reservaServicio;
        this.clienteServicio = clienteServicio;
        this.entrenadorServicio = entrenadorServicio;
        this.actividadServicio = actividadServicio;
        this.salaServicio = salaServicio;
        this.maquinariaServicio = maquinariaServicio;
    }

    private void cargarCombos(Model model) {
        model.addAttribute("clientes", clienteServicio.listarTodos());
        model.addAttribute("entrenadores", entrenadorServicio.listarTodos());
        model.addAttribute("actividades", actividadServicio.listarTodas());
        model.addAttribute("salas", salaServicio.listarTodas());
        model.addAttribute("estados", Estado.values());
        model.addAttribute("maquinarias", maquinariaServicio.listarTodas());
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
        cargarCombos(model);
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
            @RequestParam(required = false) List<Long> maquinarias,
            @RequestParam(required = false) List<Integer> cantidades,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            Cliente cliente = clienteServicio.obtenerClientePorId(clienteId);
            Entrenador entrenador = entrenadorServicio.obtenerEntrenadorPorId(entrenadorId);
            Actividad actividad = actividadServicio.obtenerActividadPorId(actividadId);
            Sala sala = salaServicio.obtenerSalaPorId(salaId);

            if (cliente == null || entrenador == null || actividad == null || sala == null) {
                model.addAttribute("regError", "Datos inválidos: cliente, entrenador, actividad o sala no encontrados.");
                cargarCombos(model);
                return "nuevaReservaAdmin";
            }

            Reserva reserva = new Reserva();
            reserva.setFechaHoraInicio(inicio);
            reserva.setFechaHoraFin(fin);
            reserva.setCliente(cliente);
            reserva.setEntrenador(entrenador);
            reserva.setActividad(actividad);
            reserva.setSala(sala);
            reserva.setComentarios(comentarios);
            reserva.setEstado(Estado.Pendiente);

            // Añadir maquinaria si se seleccionó
            if (maquinarias != null && cantidades != null && !maquinarias.isEmpty()) {
                List<ReservaMaquinaria> reservaMaquinarias = new ArrayList<>();
                for (int i = 0; i < maquinarias.size(); i++) {
                    Maquinaria maq = maquinariaServicio.obtenerMaquinariaPorId(maquinarias.get(i));
                    if (maq != null) {
                        ReservaMaquinaria rm = new ReservaMaquinaria(reserva, maq, cantidades.get(i));
                        reservaMaquinarias.add(rm);
                    }
                }
                reserva.setMaquinariaAsignada(reservaMaquinarias);
            }

            boolean ok = reservaServicio.crearReserva(reserva);

            if (!ok) {
                model.addAttribute("regError", "No se ha podido crear la reserva. Revisa horarios y disponibilidad.");
                cargarCombos(model);
                return "nuevaReservaAdmin";
            }

            redirectAttributes.addFlashAttribute("mensajeExito", "Reserva creada correctamente.");
            return "redirect:/admin/reservas";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("regError", ex.getMessage());
            cargarCombos(model);
            return "nuevaReservaAdmin";
        } catch (Exception ex) {
            model.addAttribute("regError", "Error inesperado al crear la reserva.");
            cargarCombos(model);
            return "nuevaReservaAdmin";
        }
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
        cargarCombos(model);
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
            @RequestParam(required = false) List<Long> maquinarias,
            @RequestParam(required = false) List<Integer> cantidades,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            Reserva existente = reservaServicio.obtenerPorId(id);
            if (existente == null) {
                redirectAttributes.addFlashAttribute("mensajeError", "La reserva no existe.");
                return "redirect:/admin/reservas";
            }

            Cliente cliente = clienteServicio.obtenerClientePorId(clienteId);
            Entrenador entrenador = entrenadorServicio.obtenerEntrenadorPorId(entrenadorId);
            Actividad actividad = actividadServicio.obtenerActividadPorId(actividadId);
            Sala sala = salaServicio.obtenerSalaPorId(salaId);

            if (cliente == null || entrenador == null || actividad == null || sala == null) {
                model.addAttribute("mensajeError", "Datos inválidos: cliente, entrenador, actividad o sala no encontrados.");
                model.addAttribute("reserva", existente);
                cargarCombos(model);
                return "editarReservaAdmin";
            }

            Reserva datos = new Reserva();
            datos.setFechaHoraInicio(inicio);
            datos.setFechaHoraFin(fin);
            datos.setCliente(cliente);
            datos.setEntrenador(entrenador);
            datos.setActividad(actividad);
            datos.setSala(sala);
            datos.setComentarios(comentarios);
            datos.setEstado(estado);

            // Maquinaria: validar que no se solicite más de la disponible
            if (maquinarias != null && cantidades != null && !maquinarias.isEmpty()) {
                List<ReservaMaquinaria> reservaMaquinarias = new ArrayList<>();
                for (int i = 0; i < maquinarias.size(); i++) {
                    Maquinaria maq = maquinariaServicio.obtenerMaquinariaPorId(maquinarias.get(i));
                    int cantidadSolicitada = 0;
                    if (i < cantidades.size() && cantidades.get(i) != null) {
                        cantidadSolicitada = cantidades.get(i);
                    }
                    if (maq != null) {
                        // Comprueba contra la cantidad total disponible en la entidad Maquinaria.
                        // Si su modelo usa otro nombre de getter (por ejemplo getStock o getCantidadDisponible),
                        // sustituir getCantidad() por el getter correspondiente.
                        if (cantidadSolicitada > maq.getCantidadTotal()) {
                            model.addAttribute("mensajeError",
                                    "No hay suficiente maquinaria '" + maq.getNombre() + "' disponible. Disponible: "
                                            + maq.getCantidadTotal() + ", solicitado: " + cantidadSolicitada);
                            model.addAttribute("reserva", existente);
                            cargarCombos(model);
                            return "editarReservaAdmin";
                        }
                        ReservaMaquinaria rm = new ReservaMaquinaria(datos, maq, cantidadSolicitada);
                        reservaMaquinarias.add(rm);
                    }
                }
                datos.setMaquinariaAsignada(reservaMaquinarias);
            }

            boolean ok = reservaServicio.actualizarReserva(id, datos);

            if (!ok) {
                model.addAttribute("mensajeError",
                        "No se ha podido actualizar la reserva. Puede que ya haya empezado o que exista solape.");
                model.addAttribute("reserva", existente);
                cargarCombos(model);
                return "editarReservaAdmin";
            }

            redirectAttributes.addFlashAttribute("mensajeExito", "Reserva actualizada correctamente.");
            return "redirect:/admin/reservas";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensajeError", "Error al actualizar la reserva");
            cargarCombos(model);
            return "editarReservaAdmin";
        } catch (Exception ex) {
            model.addAttribute("mensajeError", "Error al actualizar la reserva");
            cargarCombos(model);
            return "editarReservaAdmin";
        }
    }

    /* ========== ELIMINAR RESERVA ========== */

    @PostMapping("/eliminar/{id}")
    public String eliminarReserva(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Reserva reserva = reservaServicio.obtenerPorId(id);
            if (reserva == null) {
                redirectAttributes.addFlashAttribute("mensajeError", "La reserva no existe.");
                return "redirect:/admin/reservas";
            }

            boolean ok = reservaServicio.eliminarReserva(reserva.getIdReserva());

            if (ok) {
                redirectAttributes.addFlashAttribute("mensajeExito", "Reserva eliminada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", "No se ha podido eliminar la reserva.");
            }

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Error al eliminar la reserva: " + ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("timestamp", System.currentTimeMillis());
        return "redirect:/admin/reservas";
    }
}