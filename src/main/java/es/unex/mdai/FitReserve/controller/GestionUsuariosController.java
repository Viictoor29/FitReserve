package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.model.Cliente;
import es.unex.mdai.FitReserve.data.model.Entrenador;
import es.unex.mdai.FitReserve.services.ClienteServicio;
import es.unex.mdai.FitReserve.services.EntrenadorServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios")
public class GestionUsuariosController {

    private final ClienteServicio clienteServicio;
    private final EntrenadorServicio entrenadorServicio;

    public GestionUsuariosController(ClienteServicio clienteServicio,
                                     EntrenadorServicio entrenadorServicio) {
        this.clienteServicio = clienteServicio;
        this.entrenadorServicio = entrenadorServicio;
    }

    /* ========== LISTA PRINCIPAL ========== */

    @GetMapping
    public String mostrarGestionUsuarios(Model model) {
        model.addAttribute("clientes", clienteServicio.listarTodos());
        model.addAttribute("entrenadores", entrenadorServicio.listarTodos());
        return "gestionUsuariosPage";
    }

    /* ========== CLIENTES ========== */

    // VER CLIENTE
    @GetMapping("/clientes/{id}")
    public String verCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteServicio.obtenerClientePorId(id);
        model.addAttribute("cliente", cliente);
        return "admin/verCliente"; // crea esta vista
    }

    // FORMULARIO EDITAR CLIENTE
    @GetMapping("/clientes/editar/{id}")
    public String mostrarEditarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteServicio.obtenerClientePorId(id);
        model.addAttribute("clienteForm", cliente);
        return "admin/editarCliente"; // crea esta vista
    }

    // PROCESAR EDITAR CLIENTE
    @PostMapping("/clientes/editar/{id}")
    public String procesarEditarCliente(@PathVariable Long id,
                                        @ModelAttribute("clienteForm") Cliente clienteForm,
                                        BindingResult result,
                                        RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/editarCliente";
        }

        // aquí solo se actualizan los campos propios de Cliente
        // (fechaNacimiento, genero, objetivos), según tu servicio
        clienteServicio.actualizarCliente(id, clienteForm);

        redirectAttributes.addFlashAttribute("mensajeExito",
                "Cliente actualizado correctamente.");
        return "redirect:/admin/usuarios";
    }

    // ELIMINAR CLIENTE
    @PostMapping("/clientes/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {

        boolean ok = clienteServicio.eliminarCliente(id);

        if (ok) {
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Cliente eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se ha podido eliminar el cliente (no existe).");
        }

        return "redirect:/admin/usuarios";
    }

    /* ========== ENTRENADORES ========== */

    // VER ENTRENADOR
    @GetMapping("/entrenadores/{id}")
    public String verEntrenador(@PathVariable Long id, Model model) {
        Entrenador entrenador = entrenadorServicio.obtenerEntrenadorPorId(id);
        model.addAttribute("entrenador", entrenador);
        return "admin/verEntrenador"; // crea esta vista
    }

    // FORMULARIO EDITAR ENTRENADOR
    @GetMapping("/entrenadores/editar/{id}")
    public String mostrarEditarEntrenador(@PathVariable Long id, Model model) {
        Entrenador entrenador = entrenadorServicio.obtenerEntrenadorPorId(id);
        model.addAttribute("entrenadorForm", entrenador);
        return "admin/editarEntrenador"; // crea esta vista
    }

    // PROCESAR EDITAR ENTRENADOR
    @PostMapping("/entrenadores/editar/{id}")
    public String procesarEditarEntrenador(@PathVariable Long id,
                                           @ModelAttribute("entrenadorForm") Entrenador entrenadorForm,
                                           BindingResult result,
                                           RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/editarEntrenador";
        }

        entrenadorServicio.actualizarEntrenador(id, entrenadorForm);

        redirectAttributes.addFlashAttribute("mensajeExito",
                "Entrenador actualizado correctamente.");
        return "redirect:/admin/usuarios";
    }

    // ELIMINAR ENTRENADOR
    @PostMapping("/entrenadores/eliminar/{id}")
    public String eliminarEntrenador(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {

        boolean ok = entrenadorServicio.eliminarEntrenador(id);

        if (ok) {
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Entrenador eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se ha podido eliminar el entrenador (no existe).");
        }

        return "redirect:/admin/usuarios";
    }
}
