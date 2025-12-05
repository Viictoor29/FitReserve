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
            // Validar que todos los recursos existen
            Cliente cliente = clienteServicio.obtenerClientePorId(clienteId);
            if (cliente == null) {
                model.addAttribute("mensajeError", "El cliente seleccionado no existe.");
                cargarCombos(model);
                return "nuevaReservaAdmin";
            }

            Entrenador entrenador = entrenadorServicio.obtenerEntrenadorPorId(entrenadorId);
            if (entrenador == null) {
                model.addAttribute("mensajeError", "El entrenador seleccionado no existe.");
                cargarCombos(model);
                return "nuevaReservaAdmin";
            }

            Actividad actividad = actividadServicio.obtenerActividadPorId(actividadId);
            if (actividad == null) {
                model.addAttribute("mensajeError", "La actividad seleccionada no existe.");
                cargarCombos(model);
                return "nuevaReservaAdmin";
            }

            Sala sala = salaServicio.obtenerSalaPorId(salaId);
            if (sala == null) {
                model.addAttribute("mensajeError", "La sala seleccionada no existe.");
                cargarCombos(model);
                return "nuevaReservaAdmin";
            }

            // Validar que fin sea posterior a inicio
            if (!fin.isAfter(inicio)) {
                model.addAttribute("mensajeError", "La fecha de fin debe ser posterior a la de inicio.");
                cargarCombos(model);
                return "nuevaReservaAdmin";
            }

            // No permitir reservas en el pasado
            if (inicio.isBefore(LocalDateTime.now())) {
                model.addAttribute("mensajeError", "No se pueden crear reservas en el pasado.");
                cargarCombos(model);
                return "nuevaReservaAdmin";
            }

            // Comprobar que el entrenador trabaja en ese horario
            if (entrenador.getHoraInicioTrabajo() != null && entrenador.getHoraFinTrabajo() != null) {
                if (inicio.toLocalTime().isBefore(entrenador.getHoraInicioTrabajo()) ||
                        fin.toLocalTime().isAfter(entrenador.getHoraFinTrabajo())) {
                    model.addAttribute("mensajeError",
                            "El entrenador no trabaja en ese horario. Horario: " +
                                    entrenador.getHoraInicioTrabajo() + " - " + entrenador.getHoraFinTrabajo());
                    cargarCombos(model);
                    return "nuevaReservaAdmin";
                }
            }

            // Comprobar disponibilidad del entrenador
            if (reservaServicio.haySolapeEntrenador(entrenador.getIdEntrenador(), inicio, fin)) {
                model.addAttribute("mensajeError",
                        "El entrenador no está disponible en el intervalo seleccionado.");
                cargarCombos(model);
                return "nuevaReservaAdmin";
            }

            // Comprobar disponibilidad de la sala
            if (reservaServicio.haySolapeSala(sala.getIdSala(), inicio, fin)) {
                model.addAttribute("mensajeError",
                        "La sala no está disponible en el intervalo seleccionado.");
                cargarCombos(model);
                return "nuevaReservaAdmin";
            }

            // Validar maquinaria solicitada
            List<ReservaMaquinaria> reservaMaquinarias = new ArrayList<>();
            if (maquinarias != null && cantidades != null && !maquinarias.isEmpty()) {
                for (int i = 0; i < maquinarias.size(); i++) {
                    Long idMaq = maquinarias.get(i);
                    Integer cantidad = (i < cantidades.size()) ? cantidades.get(i) : 0;

                    if (cantidad == null || cantidad <= 0) {
                        continue;
                    }

                    Maquinaria maq = maquinariaServicio.obtenerMaquinariaPorId(idMaq);
                    if (maq == null) {
                        model.addAttribute("mensajeError", "Maquinaria no encontrada.");
                        cargarCombos(model);
                        return "nuevaReservaAdmin";
                    }

                    // Verificar disponibilidad
                    int totalEnUso = reservaServicio.totalMaquinariaReservadaEnIntervalo(
                            idMaq, inicio, fin, actividad.getTipoActividad()
                    );

                    if (totalEnUso + cantidad > maq.getCantidadTotal()) {
                        model.addAttribute("mensajeError",
                                "No hay suficiente maquinaria '" + maq.getNombre() + "' disponible. " +
                                        "Disponible: " + (maq.getCantidadTotal() - totalEnUso) +
                                        ", solicitado: " + cantidad);
                        cargarCombos(model);
                        return "nuevaReservaAdmin";
                    }
                }
            }

            // Crear la reserva
            Reserva reserva = new Reserva();
            reserva.setFechaHoraInicio(inicio);
            reserva.setFechaHoraFin(fin);
            reserva.setCliente(cliente);
            reserva.setEntrenador(entrenador);
            reserva.setActividad(actividad);
            reserva.setSala(sala);
            reserva.setComentarios(comentarios);
            reserva.setEstado(Estado.Pendiente);

            // Añadir maquinaria
            if (maquinarias != null && cantidades != null && !maquinarias.isEmpty()) {
                for (int i = 0; i < maquinarias.size(); i++) {
                    Integer cantidad = (i < cantidades.size()) ? cantidades.get(i) : 0;
                    if (cantidad != null && cantidad > 0) {
                        Maquinaria maq = maquinariaServicio.obtenerMaquinariaPorId(maquinarias.get(i));
                        if (maq != null) {
                            ReservaMaquinaria rm = new ReservaMaquinaria(reserva, maq, cantidad);
                            reservaMaquinarias.add(rm);
                        }
                    }
                }
                reserva.setMaquinariaAsignada(reservaMaquinarias);
            }

            boolean ok = reservaServicio.crearReserva(reserva);

            if (!ok) {
                model.addAttribute("mensajeError",
                        "No se pudo crear la reserva. Verifica disponibilidad.");
                cargarCombos(model);
                return "nuevaReservaAdmin";
            }

            redirectAttributes.addFlashAttribute("mensajeExito", "Reserva creada exitosamente.");
            return "redirect:/admin/reservas";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensajeError", "Error: " + ex.getMessage());
            cargarCombos(model);
            return "nuevaReservaAdmin";
        } catch (Exception ex) {
            model.addAttribute("mensajeError", "Error inesperado: " + ex.getMessage());
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

            // Validar que todos los recursos existen
            Cliente cliente = clienteServicio.obtenerClientePorId(clienteId);
            if (cliente == null) {
                model.addAttribute("mensajeError", "El cliente seleccionado no existe.");
                model.addAttribute("reserva", existente);
                cargarCombos(model);
                return "editarReservaAdmin";
            }

            Entrenador entrenador = entrenadorServicio.obtenerEntrenadorPorId(entrenadorId);
            if (entrenador == null) {
                model.addAttribute("mensajeError", "El entrenador seleccionado no existe.");
                model.addAttribute("reserva", existente);
                cargarCombos(model);
                return "editarReservaAdmin";
            }

            Actividad actividad = actividadServicio.obtenerActividadPorId(actividadId);
            if (actividad == null) {
                model.addAttribute("mensajeError", "La actividad seleccionada no existe.");
                model.addAttribute("reserva", existente);
                cargarCombos(model);
                return "editarReservaAdmin";
            }

            Sala sala = salaServicio.obtenerSalaPorId(salaId);
            if (sala == null) {
                model.addAttribute("mensajeError", "La sala seleccionada no existe.");
                model.addAttribute("reserva", existente);
                cargarCombos(model);
                return "editarReservaAdmin";
            }

            // Validar lógica de fechas
            if (!fin.isAfter(inicio)) {
                model.addAttribute("mensajeError", "La fecha de fin debe ser posterior a la de inicio.");
                model.addAttribute("reserva", existente);
                cargarCombos(model);
                return "editarReservaAdmin";
            }

            if (inicio.isBefore(LocalDateTime.now())) {
                model.addAttribute("mensajeError", "No se pueden programar reservas en el pasado.");
                model.addAttribute("reserva", existente);
                cargarCombos(model);
                return "editarReservaAdmin";
            }

            // Comprobar horario de trabajo del entrenador
            if (entrenador.getHoraInicioTrabajo() != null && entrenador.getHoraFinTrabajo() != null) {
                if (inicio.toLocalTime().isBefore(entrenador.getHoraInicioTrabajo()) ||
                        fin.toLocalTime().isAfter(entrenador.getHoraFinTrabajo())) {
                    model.addAttribute("mensajeError",
                            "El entrenador no trabaja en ese horario. Horario: " +
                                    entrenador.getHoraInicioTrabajo() + " - " + entrenador.getHoraFinTrabajo());
                    model.addAttribute("reserva", existente);
                    cargarCombos(model);
                    return "editarReservaAdmin";
                }
            }

            // Comprobar disponibilidad del entrenador (ignorando la propia reserva)
            if (reservaServicio.haySolapeEntrenador(entrenador.getIdEntrenador(), inicio, fin)) {
                boolean solapeReal = false;
                List<Reserva> reservasEntrenador = reservaServicio.listarHistorialEntrenador(
                        entrenador.getIdEntrenador()
                );
                for (Reserva r : reservasEntrenador) {
                    if (r.getIdReserva().equals(id) || r.getEstado() != Estado.Pendiente) continue;
                    if (r.getFechaHoraInicio().isBefore(fin) && r.getFechaHoraFin().isAfter(inicio)) {
                        solapeReal = true;
                        break;
                    }
                }
                if (solapeReal) {
                    model.addAttribute("mensajeError",
                            "El entrenador no está disponible en el intervalo seleccionado.");
                    model.addAttribute("reserva", existente);
                    cargarCombos(model);
                    return "editarReservaAdmin";
                }
            }

            // Comprobar disponibilidad de la sala (ignorando la propia reserva)
            if (reservaServicio.haySolapeSala(sala.getIdSala(), inicio, fin)) {
                boolean solapeRealSala = false;
                List<Reserva> todasReservas = reservaServicio.listarTodas();
                for (Reserva r : todasReservas) {
                    if (r.getIdReserva().equals(id) || r.getEstado() != Estado.Pendiente) continue;
                    if (r.getSala() == null || !sala.getIdSala().equals(r.getSala().getIdSala())) continue;
                    if (r.getFechaHoraInicio().isBefore(fin) && r.getFechaHoraFin().isAfter(inicio)) {
                        solapeRealSala = true;
                        break;
                    }
                }
                if (solapeRealSala) {
                    model.addAttribute("mensajeError",
                            "La sala no está disponible en el intervalo seleccionado.");
                    model.addAttribute("reserva", existente);
                    cargarCombos(model);
                    return "editarReservaAdmin";
                }
            }

            // Validar y actualizar maquinaria
            List<ReservaMaquinaria> reservaMaquinarias = new ArrayList<>();
            if (maquinarias != null && cantidades != null && !maquinarias.isEmpty()) {
                for (int i = 0; i < maquinarias.size(); i++) {
                    Integer cantidad = (i < cantidades.size()) ? cantidades.get(i) : 0;
                    if (cantidad == null || cantidad <= 0) continue;

                    Maquinaria maq = maquinariaServicio.obtenerMaquinariaPorId(maquinarias.get(i));
                    if (maq == null) continue;

                    // Verificar disponibilidad
                    int totalEnUso = reservaServicio.totalMaquinariaReservadaEnIntervalo(
                            maq.getIdMaquinaria(), inicio, fin, actividad.getTipoActividad()
                    );

                    if (totalEnUso + cantidad > maq.getCantidadTotal()) {
                        model.addAttribute("mensajeError",
                                "No hay suficiente maquinaria '" + maq.getNombre() + "' disponible. " +
                                        "Disponible: " + (maq.getCantidadTotal() - totalEnUso) +
                                        ", solicitado: " + cantidad);
                        model.addAttribute("reserva", existente);
                        cargarCombos(model);
                        return "editarReservaAdmin";
                    }

                    ReservaMaquinaria rm = new ReservaMaquinaria(existente, maq, cantidad);
                    reservaMaquinarias.add(rm);
                }
            }

            // Crear objeto con todos los datos actualizados
            Reserva datos = new Reserva();
            datos.setFechaHoraInicio(inicio);
            datos.setFechaHoraFin(fin);
            datos.setCliente(cliente);
            datos.setEntrenador(entrenador);
            datos.setActividad(actividad);
            datos.setSala(sala);
            datos.setComentarios(comentarios);
            datos.setEstado(estado);
            datos.setMaquinariaAsignada(reservaMaquinarias);

            boolean ok = reservaServicio.actualizarReserva(id, datos);

            if (!ok) {
                model.addAttribute("mensajeError",
                        "No se pudo actualizar la reserva. Verifica disponibilidad.");
                model.addAttribute("reserva", existente);
                cargarCombos(model);
                return "editarReservaAdmin";
            }

            redirectAttributes.addFlashAttribute("mensajeExito", "Reserva actualizada correctamente.");
            return "redirect:/admin/reservas";

        } catch (Exception ex) {
            model.addAttribute("mensajeError", "Error al actualizar la reserva: " + ex.getMessage());
            Reserva existente = reservaServicio.obtenerPorId(id);
            model.addAttribute("reserva", existente);
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