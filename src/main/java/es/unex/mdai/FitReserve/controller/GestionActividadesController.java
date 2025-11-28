package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.enume.NivelActividad;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Actividad;
import es.unex.mdai.FitReserve.services.ActividadServicio;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/actividades")
public class GestionActividadesController {

    private final ActividadServicio actividadServicio;

    public GestionActividadesController(ActividadServicio actividadServicio) {
        this.actividadServicio = actividadServicio;
    }

    /* ========== LISTADO PRINCIPAL ========== */

    @GetMapping
    public String listarActividades(Model model) {
        model.addAttribute("actividades", actividadServicio.listarTodas());
        return "gestionActividadesPage";
    }

    private void cargarListasEnums(Model model) {
        model.addAttribute("tiposActividad", TipoActividad.values());
        model.addAttribute("nivelesActividad", NivelActividad.values());
    }

    /* ========== NUEVA ACTIVIDAD ========== */

    @GetMapping("/nueva")
    public String mostrarNuevaActividad(Model model) {
        if (!model.containsAttribute("actividadForm")) {
            model.addAttribute("actividadForm", new Actividad());
        }
        cargarListasEnums(model);
        return "nuevaActividad";
    }

    @PostMapping("/nueva")
    public String procesarNuevaActividad(
            @Valid @ModelAttribute("actividadForm") Actividad actividadForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        cargarListasEnums(model);

        if (bindingResult.hasErrors()) {
            model.addAttribute("regError", "El registro no ha funcionado. Revisa los campos.");
            return "nuevaActividad";
        }

        if (actividadForm == null) {
            model.addAttribute("regError", "Datos de actividad incompletos.");
            return "nuevaActividad";
        }

        try {
            boolean creada = actividadServicio.crearActividad(actividadForm);

            if (!creada) {
                model.addAttribute("regError", "No se ha podido registrar la actividad. Revisa nombre y datos.");
                return "nuevaActividad";
            }

            redirectAttributes.addFlashAttribute("mensajeExito", "Actividad creada correctamente.");
            return "redirect:/admin/actividades";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("regError", ex.getMessage());
            return "nuevaActividad";
        } catch (Exception ex) {
            model.addAttribute("regError", "Error inesperado durante el registro de la actividad.");
            return "nuevaActividad";
        }
    }

    /* ========== EDITAR ACTIVIDAD ========== */

    @GetMapping("/editar/{id}")
    public String mostrarEditarActividad(@PathVariable Long id,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        Actividad actividad = actividadServicio.obtenerActividadPorId(id);

        if (actividad == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "Actividad no encontrada.");
            return "redirect:/admin/actividades";
        }

        model.addAttribute("actividadForm", actividad);
        cargarListasEnums(model);
        return "editarActividad";
    }

    @PostMapping("/editar/{id}")
    public String procesarEditarActividad(
            @PathVariable Long id,
            @Valid @ModelAttribute("actividadForm") Actividad actividadForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        cargarListasEnums(model);

        if (bindingResult.hasErrors()) {
            return "editarActividad";
        }

        try {
            Actividad existente = actividadServicio.obtenerActividadPorId(id);
            if (existente == null) {
                redirectAttributes.addFlashAttribute("mensajeError", "Actividad no encontrada.");
                return "redirect:/admin/actividades";
            }

            // Actualizamos campos desde el form
            existente.setNombre(actividadForm.getNombre());
            existente.setDescripcion(actividadForm.getDescripcion());
            existente.setTipoActividad(actividadForm.getTipoActividad());
            existente.setNivel(actividadForm.getNivel());

            boolean ok = actividadServicio.actualizarActividad(id, existente);

            if (!ok) {
                model.addAttribute("mensajeError", "No se ha podido actualizar la actividad.");
                return "editarActividad";
            }

            redirectAttributes.addFlashAttribute("mensajeExito", "Actividad actualizada correctamente.");
            return "redirect:/admin/actividades";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensajeError", ex.getMessage());
            return "editarActividad";
        } catch (Exception ex) {
            model.addAttribute("mensajeError", "Error al actualizar la actividad: " + ex.getMessage());
            return "editarActividad";
        }
    }

    /* ========== ELIMINAR ACTIVIDAD ========== */

    @PostMapping("/eliminar/{id}")
    public String eliminarActividad(@PathVariable Long id,
                                    RedirectAttributes redirectAttributes) {
        try {
            Actividad actividad = actividadServicio.obtenerActividadPorId(id);
            if (actividad == null) {
                redirectAttributes.addFlashAttribute("mensajeError", "Actividad no encontrada.");
                return "redirect:/admin/actividades";
            }

            boolean ok = actividadServicio.eliminarActividad(actividad.getIdActividad());

            if (ok) {
                redirectAttributes.addFlashAttribute("mensajeExito", "Actividad eliminada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", "No se ha podido eliminar la actividad.");
            }

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Error al eliminar la actividad: " + ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("timestamp", System.currentTimeMillis());
        return "redirect:/admin/actividades";
    }
}
