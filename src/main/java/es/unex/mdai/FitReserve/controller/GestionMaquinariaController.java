package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Maquinaria;
import es.unex.mdai.FitReserve.data.model.ReservaMaquinaria;
import es.unex.mdai.FitReserve.data.repository.ReservaMaquinariaRepository;
import es.unex.mdai.FitReserve.services.MaquinariaServicio;
import es.unex.mdai.FitReserve.services.ReservaServicio;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/maquinaria")
public class GestionMaquinariaController {

    private final MaquinariaServicio maquinariaServicio;
    private final ReservaServicio reservaServicio;
    private final ReservaMaquinariaRepository reservaMaquinariaRepository;

    public GestionMaquinariaController(MaquinariaServicio maquinariaServicio,
                                       ReservaServicio reservaServicio,
                                       ReservaMaquinariaRepository reservaMaquinariaRepository) {
        this.maquinariaServicio = maquinariaServicio;
        this.reservaServicio = reservaServicio;
        this.reservaMaquinariaRepository = reservaMaquinariaRepository;
    }

    private void cargarListasEnums(Model model) {
        model.addAttribute("tiposActividad", TipoActividad.values());
    }

    @GetMapping
    public String mostrarGestionMaquinaria(Model model) {
        model.addAttribute("maquinarias", maquinariaServicio.listarTodas());
        return "gestionMaquinariaPage";
    }

    @GetMapping("/nueva")
    public String mostrarNuevaMaquinaria(Model model) {
        if (!model.containsAttribute("maquinariaForm")) {
            model.addAttribute("maquinariaForm", new Maquinaria());
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

        try {
            boolean ok = maquinariaServicio.crearMaquinaria(maquinariaForm);

            if (!ok) {
                model.addAttribute("regError",
                        "No se ha podido crear la maquinaria. Revisa el nombre (no duplicado) y los campos obligatorios.");
                return "nuevaMaquinaria";
            }

            redirectAttributes.addFlashAttribute("mensajeExito", "Maquinaria creada correctamente.");
            return "redirect:/admin/maquinaria";

        } catch (Exception ex) {
            model.addAttribute("regError", "Error al crear la maquinaria: " + ex.getMessage());
            return "nuevaMaquinaria";
        }
    }

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

            existente.setNombre(maquinariaForm.getNombre());
            existente.setCantidadTotal(maquinariaForm.getCantidadTotal());
            existente.setTipoActividad(maquinariaForm.getTipoActividad());
            existente.setDescripcion(maquinariaForm.getDescripcion());

            boolean ok = maquinariaServicio.actualizarMaquinaria(id, existente);

            if (!ok) {
                model.addAttribute("mensajeError", "No se ha podido actualizar la maquinaria.");
                return "editarMaquinaria";
            }

            redirectAttributes.addFlashAttribute("mensajeExito", "Maquinaria actualizada correctamente.");
            return "redirect:/admin/maquinaria";

        } catch (Exception ex) {
            model.addAttribute("mensajeError", "Error al actualizar la maquinaria: " + ex.getMessage());
            return "editarMaquinaria";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarMaquinaria(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            Maquinaria maquinaria = maquinariaServicio.obtenerMaquinariaPorId(id);
            if (maquinaria == null) {
                redirectAttributes.addFlashAttribute("mensajeError", "Maquinaria no encontrada.");
                return "redirect:/admin/maquinaria";
            }

            // Obtener relaciones usando el repositorio directamente (evita problema LAZY)
            List<ReservaMaquinaria> relaciones = reservaMaquinariaRepository.findByMaquinariaIdMaquinaria(id);

            // Extraer IDs únicos de reservas
            Set<Long> reservaIds = relaciones.stream()
                    .map(ReservaMaquinaria::getReserva)
                    .filter(Objects::nonNull)
                    .map(r -> r.getIdReserva())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Eliminar cada reserva asociada
            for (Long reservaId : reservaIds) {
                try {
                    reservaServicio.eliminarReserva(reservaId);
                } catch (Exception ex) {
                    // Continuar con las demás aunque falle una
                }
            }

            // Finalmente eliminar la maquinaria
            boolean ok = maquinariaServicio.eliminarMaquinaria(id);

            if (ok) {
                redirectAttributes.addFlashAttribute("mensajeExito",
                        "Maquinaria y reservas asociadas eliminadas correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("mensajeError",
                        "No se ha podido eliminar la maquinaria.");
            }

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Error al eliminar la maquinaria: " + ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("timestamp", System.currentTimeMillis());
        return "redirect:/admin/maquinaria";
    }
}