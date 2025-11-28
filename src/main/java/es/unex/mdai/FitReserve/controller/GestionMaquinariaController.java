package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Maquinaria;
import es.unex.mdai.FitReserve.services.MaquinariaServicio;
import jakarta.validation.Valid;
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

    private void cargarListasEnums(Model model) {
        model.addAttribute("tiposActividad", TipoActividad.values());
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
        if (!model.containsAttribute("maquinariaForm")) {
            Maquinaria maquinaria = new Maquinaria();
            model.addAttribute("maquinariaForm", maquinaria);
        }
        cargarListasEnums(model);
        return "nuevaMaquinaria";
    }

    @PostMapping("/nueva")
    public String procesarNuevaMaquinaria(
            @Valid @ModelAttribute("maquinariaForm") Maquinaria maquinariaForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        cargarListasEnums(model);

        if (bindingResult.hasErrors()) {
            model.addAttribute("regError", "El registro no ha funcionado. Revisa los campos.");
            return "nuevaMaquinaria";
        }

        if (maquinariaForm == null) {
            model.addAttribute("regError", "Datos de maquinaria incompletos.");
            return "nuevaMaquinaria";
        }

        try {
            boolean ok = maquinariaServicio.crearMaquinaria(maquinariaForm);

            if (!ok) {
                model.addAttribute("regError",
                        "No se ha podido crear la maquinaria. Revisa el nombre (no duplicado) y los campos obligatorios.");
                return "nuevaMaquinaria";
            }

            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Maquinaria creada correctamente.");
            return "redirect:/admin/maquinaria";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("regError", ex.getMessage());
            return "nuevaMaquinaria";
        } catch (Exception ex) {
            model.addAttribute("regError", "Error inesperado al crear la maquinaria.");
            return "nuevaMaquinaria";
        }
    }

    /* ========== EDITAR MAQUINARIA ========== */

    @GetMapping("/editar/{id}")
    public String mostrarEditarMaquinaria(@PathVariable Long id,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        Maquinaria maquinaria = maquinariaServicio.obtenerMaquinariaPorId(id);
        if (maquinaria == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "Maquinaria no encontrada.");
            return "redirect:/admin/maquinaria";
        }

        model.addAttribute("maquinariaForm", maquinaria);
        cargarListasEnums(model);
        return "editarMaquinaria";
    }

    @PostMapping("/editar/{id}")
    public String procesarEditarMaquinaria(
            @PathVariable Long id,
            @Valid @ModelAttribute("maquinariaForm") Maquinaria maquinariaForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        cargarListasEnums(model);

        if (bindingResult.hasErrors()) {
            return "editarMaquinaria";
        }

        try {
            Maquinaria existente = maquinariaServicio.obtenerMaquinariaPorId(id);
            if (existente == null) {
                redirectAttributes.addFlashAttribute("mensajeError", "Maquinaria no encontrada.");
                return "redirect:/admin/maquinaria";
            }

            // Actualizamos campos desde el formulario
            existente.setNombre(maquinariaForm.getNombre());
            existente.setCantidadTotal(maquinariaForm.getCantidadTotal());
            existente.setTipoActividad(maquinariaForm.getTipoActividad());
            existente.setDescripcion(maquinariaForm.getDescripcion());

            boolean ok = maquinariaServicio.actualizarMaquinaria(id, existente);

            if (!ok) {
                model.addAttribute("mensajeError", "No se ha podido actualizar la maquinaria.");
                return "editarMaquinaria";
            }

            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Maquinaria actualizada correctamente.");
            return "redirect:/admin/maquinaria";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensajeError", ex.getMessage());
            return "editarMaquinaria";
        } catch (Exception ex) {
            model.addAttribute("mensajeError", "Error al actualizar la maquinaria: " + ex.getMessage());
            return "editarMaquinaria";
        }
    }

    /* ========== ELIMINAR MAQUINARIA ========== */

    @PostMapping("/eliminar/{id}")
    public String eliminarMaquinaria(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            Maquinaria maquinaria = maquinariaServicio.obtenerMaquinariaPorId(id);
            if (maquinaria == null) {
                redirectAttributes.addFlashAttribute("mensajeError", "Maquinaria no encontrada.");
                return "redirect:/admin/maquinaria";
            }

            boolean ok = maquinariaServicio.eliminarMaquinaria(maquinaria.getIdMaquinaria());

            if (ok) {
                redirectAttributes.addFlashAttribute("mensajeExito",
                        "Maquinaria eliminada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("mensajeError",
                        "No se ha podido eliminar la maquinaria (no existe o está en uso).");
            }

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Error al eliminar la maquinaria: " + ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("timestamp", System.currentTimeMillis());
        return "redirect:/admin/maquinaria";
    }
}
