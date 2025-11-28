package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Maquinaria;
import es.unex.mdai.FitReserve.services.MaquinariaServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/maquinaria")
public class GestionMaquinariaController {

    private final MaquinariaServicio maquinariaServicio;

    public GestionMaquinariaController(MaquinariaServicio maquinariaServicio) {
        this.maquinariaServicio = maquinariaServicio;
    }

    /* ========== LISTADO PRINCIPAL ========== */

    @GetMapping
    public String mostrarGestionMaquinaria(Model model) {
        model.addAttribute("maquinarias", maquinariaServicio.listarTodas());
        return "gestionMaquinariaPage";
    }

    /* ========== NUEVA MAQUINARIA ========== */

    @GetMapping("/nueva")
    public String mostrarNuevaMaquinaria(Model model) {
        Maquinaria maquinaria = new Maquinaria();
        model.addAttribute("maquinariaForm", maquinaria);
        model.addAttribute("tiposActividad", TipoActividad.values());
        return "nuevaMaquinaria";
    }

    @PostMapping("/nueva")
    public String procesarNuevaMaquinaria(
            @ModelAttribute("maquinariaForm") Maquinaria maquinariaForm,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("tiposActividad", TipoActividad.values());
            return "nuevaMaquinaria";
        }

        boolean ok = maquinariaServicio.crearMaquinaria(maquinariaForm);

        if (!ok) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se ha podido crear la maquinaria. Revisa el nombre (no duplicado) y los campos obligatorios.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Maquinaria creada correctamente.");
        }

        return "redirect:/admin/maquinaria";
    }

    /* ========== EDITAR MAQUINARIA ========== */

    @GetMapping("/editar/{id}")
    public String mostrarEditarMaquinaria(@PathVariable Long id, Model model) {
        Maquinaria maquinaria = maquinariaServicio.obtenerMaquinariaPorId(id);
        model.addAttribute("maquinariaForm", maquinaria);
        model.addAttribute("tiposActividad", TipoActividad.values());
        return "editarMaquinaria";
    }

    @PostMapping("/editar/{id}")
    public String procesarEditarMaquinaria(
            @PathVariable Long id,
            @ModelAttribute("maquinariaForm") Maquinaria maquinariaForm,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("tiposActividad", TipoActividad.values());
            return "editarMaquinaria";
        }

        boolean ok = maquinariaServicio.actualizarMaquinaria(id, maquinariaForm);

        if (!ok) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se ha podido actualizar la maquinaria.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Maquinaria actualizada correctamente.");
        }

        return "redirect:/admin/maquinaria";
    }

    /* ========== ELIMINAR MAQUINARIA ========== */

    @PostMapping("/eliminar/{id}")
    public String eliminarMaquinaria(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {

        boolean ok = maquinariaServicio.eliminarMaquinaria(id);

        if (ok) {
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Maquinaria eliminada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se ha podido eliminar la maquinaria (no existe o está en uso).");
        }

        return "redirect:/admin/maquinaria";
    }
}
