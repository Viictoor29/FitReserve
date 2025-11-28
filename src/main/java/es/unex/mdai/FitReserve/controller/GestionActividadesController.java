package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.enume.NivelActividad;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Actividad;
import es.unex.mdai.FitReserve.services.ActividadServicio;
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
    public String mostrarGestionActividades(Model model) {
        model.addAttribute("actividades", actividadServicio.listarTodas());
        return "gestionActividadesPage";
    }

    /* ========== NUEVA ACTIVIDAD ========== */

    @GetMapping("/nueva")
    public String mostrarNuevaActividad(Model model) {
        Actividad actividad = new Actividad();
        model.addAttribute("actividadForm", actividad);
        model.addAttribute("tiposActividad", TipoActividad.values());
        model.addAttribute("nivelesActividad", NivelActividad.values());
        return "nuevaActividad";
    }

    @PostMapping("/nueva")
    public String procesarNuevaActividad(
            @ModelAttribute("actividadForm") Actividad actividadForm,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("tiposActividad", TipoActividad.values());
            model.addAttribute("nivelesActividad", NivelActividad.values());
            return "nuevaActividad";
        }

        boolean ok = actividadServicio.crearActividad(actividadForm);

        if (!ok) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se ha podido crear la actividad. Revisa el nombre (no duplicado) y los campos obligatorios.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Actividad creada correctamente.");
        }

        return "redirect:/admin/actividades";
    }

    /* ========== EDITAR ACTIVIDAD ========== */

    @GetMapping("/editar/{id}")
    public String mostrarEditarActividad(@PathVariable Long id, Model model) {
        Actividad actividad = actividadServicio.obtenerActividadPorId(id);
        model.addAttribute("actividadForm", actividad);
        model.addAttribute("tiposActividad", TipoActividad.values());
        model.addAttribute("nivelesActividad", NivelActividad.values());
        return "editarActividad";
    }

    @PostMapping("/editar/{id}")
    public String procesarEditarActividad(
            @PathVariable Long id,
            @ModelAttribute("actividadForm") Actividad actividadForm,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("tiposActividad", TipoActividad.values());
            model.addAttribute("nivelesActividad", NivelActividad.values());
            return "editarActividad";
        }

        boolean ok = actividadServicio.actualizarActividad(id, actividadForm);

        if (!ok) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se ha podido actualizar la actividad.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Actividad actualizada correctamente.");
        }

        return "redirect:/admin/actividades";
    }

    /* ========== ELIMINAR ACTIVIDAD ========== */

    @PostMapping("/eliminar/{id}")
    public String eliminarActividad(@PathVariable Long id,
                                    RedirectAttributes redirectAttributes) {

        boolean ok = actividadServicio.eliminarActividad(id);

        if (ok) {
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Actividad eliminada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se ha podido eliminar la actividad (no existe o está en uso).");
        }

        return "redirect:/admin/actividades";
    }
}
