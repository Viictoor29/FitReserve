package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.model.Sala;
import es.unex.mdai.FitReserve.services.SalaServicio;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/salas")
public class GestionSalasController {

    private final SalaServicio salaServicio;

    public GestionSalasController(SalaServicio salaServicio) {
        this.salaServicio = salaServicio;
    }

    /* ========== LISTADO PRINCIPAL ========== */

    @GetMapping
    public String listarSalas(Model model) {
        model.addAttribute("salas", salaServicio.listarTodas());
        return "gestionSalasPage";
    }

    /* ========== NUEVA SALA ========== */

    // FORM NUEVA SALA
    @GetMapping("/nueva")
    public String mostrarNuevaSala(Model model) {
        if (!model.containsAttribute("salaForm")) {
            model.addAttribute("salaForm", new Sala());
        }
        return "nuevaSala";
    }

    // POST NUEVA SALA
    @PostMapping("/nueva")
    public String procesarNuevaSala(
            @Valid @ModelAttribute("salaForm") Sala salaForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Errores de validación/binding
        if (bindingResult.hasErrors()) {
            model.addAttribute("regError", "El registro no ha funcionado. Revisa los campos.");
            return "nuevaSala";
        }

        if (salaForm == null) {
            model.addAttribute("regError", "Datos de sala incompletos.");
            return "nuevaSala";
        }

        try {
            boolean creada = salaServicio.crearSala(salaForm);

            if (!creada) {
                model.addAttribute("regError", "No se ha podido registrar la sala. Revisa nombre y capacidad.");
                return "nuevaSala";
            }

            redirectAttributes.addFlashAttribute("mensajeExito", "Sala creada correctamente.");
            return "redirect:/admin/salas";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("regError", ex.getMessage());
            return "nuevaSala";
        } catch (Exception ex) {
            model.addAttribute("regError", "Error inesperado durante el registro de la sala.");
            return "nuevaSala";
        }
    }

    /* ========== EDITAR SALA ========== */

    // FORM EDITAR
    @GetMapping("/editar/{id}")
    public String mostrarEditarSala(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Sala sala = salaServicio.obtenerSalaPorId(id);

        if (sala == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "Sala no encontrada.");
            return "redirect:/admin/salas";
        }

        model.addAttribute("salaForm", sala);
        return "editarSala";
    }

    // POST EDITAR
    @PostMapping("/editar/{id}")
    public String procesarEditarSala(
            @PathVariable Long id,
            @Valid @ModelAttribute("salaForm") Sala salaForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "editarSala";
        }

        try {
            Sala salaExistente = salaServicio.obtenerSalaPorId(id);
            if (salaExistente == null) {
                redirectAttributes.addFlashAttribute("mensajeError", "Sala no encontrada.");
                return "redirect:/admin/salas";
            }

            // Actualizamos campos (por si no quieres confiar en el ID del form)
            salaExistente.setNombre(salaForm.getNombre());
            salaExistente.setCapacidad(salaForm.getCapacidad());
            salaExistente.setUbicacion(salaForm.getUbicacion());
            salaExistente.setDescripcion(salaForm.getDescripcion());

            salaServicio.actualizarSala(id, salaExistente);

            redirectAttributes.addFlashAttribute("mensajeExito", "Sala actualizada correctamente.");
            return "redirect:/admin/salas";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensajeError", ex.getMessage());
            return "editarSala";
        } catch (Exception ex) {
            model.addAttribute("mensajeError", "Error al actualizar la sala: " + ex.getMessage());
            return "editarSala";
        }
    }

    /* ========== ELIMINAR SALA ========== */

    @PostMapping("/eliminar/{id}")
    public String eliminarSala(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Igual que en GestionUsuarios: primero recuperar la entidad
            Sala sala = salaServicio.obtenerSalaPorId(id);
            if (sala == null) {
                redirectAttributes.addFlashAttribute("mensajeError", "Sala no encontrada.");
                return "redirect:/admin/salas";
            }

            boolean ok = salaServicio.eliminarSala(sala.getIdSala());

            if (ok) {
                redirectAttributes.addFlashAttribute("mensajeExito", "Sala eliminada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", "No se ha podido eliminar la sala.");
            }

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Error al eliminar la sala: " + e.getMessage());
        }

        // Igual que en GestionUsuarios: truco para evitar caché
        redirectAttributes.addFlashAttribute("timestamp", System.currentTimeMillis());
        return "redirect:/admin/salas";
    }
}
