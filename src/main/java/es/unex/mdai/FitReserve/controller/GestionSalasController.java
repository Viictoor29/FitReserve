package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.model.Sala;
import es.unex.mdai.FitReserve.services.SalaServicio;
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

    // LISTADO PRINCIPAL
    @GetMapping
    public String listarSalas(Model model) {
        model.addAttribute("salas", salaServicio.listarTodas()); // o listarTodas()
        return "gestionSalasPage";
    }

    // NUEVA SALA - FORM
    @GetMapping("/nueva")
    public String mostrarNuevaSala(Model model) {
        model.addAttribute("salaForm", new Sala());
        return "nuevaSala";
    }

    // NUEVA SALA - POST
    @PostMapping("/nueva")
    public String procesarNuevaSala(@ModelAttribute("salaForm") Sala salaForm,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {

        // Errores de binding (tipos incorrectos, etc.)
        if (result.hasErrors()) {
            return "nuevaSala";
        }

        boolean ok = salaServicio.crearSala(salaForm);

        if (!ok) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se ha podido registrar la sala. Revisa nombre y capacidad.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Sala creada correctamente.");
        }

        return "redirect:/admin/salas";
    }

    // EDITAR SALA - FORM
    @GetMapping("/editar/{id}")
    public String mostrarEditarSala(@PathVariable Long id, Model model) {
        Sala sala = salaServicio.obtenerSalaPorId(id);
        model.addAttribute("salaForm", sala);
        return "editarSala";
    }

    // EDITAR SALA - POST
    @PostMapping("/editar/{id}")
    public String procesarEditarSala(@PathVariable Long id,
                                     @ModelAttribute("salaForm") Sala salaForm,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "editarSala";
        }

        salaServicio.actualizarSala(id, salaForm);

        redirectAttributes.addFlashAttribute("mensajeExito",
                "Sala actualizada correctamente.");
        return "redirect:/admin/salas";
    }

    // ELIMINAR SALA (POST con confirm JS)
    @PostMapping("/eliminar/{id}")
    public String eliminarSala(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {

        boolean ok = salaServicio.eliminarSala(id);

        if (ok) {
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Sala eliminada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se ha podido eliminar la sala (no existe).");
        }

        return "redirect:/admin/salas";
    }
}
