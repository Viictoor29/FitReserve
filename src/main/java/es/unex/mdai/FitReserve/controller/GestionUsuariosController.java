package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.services.ClienteServicio;
import es.unex.mdai.FitReserve.services.EntrenadorServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping
    public String mostrarGestionUsuarios(Model model) {

        model.addAttribute("clientes", clienteServicio.listarTodos());
        model.addAttribute("entrenadores", entrenadorServicio.listarTodos());

        return "gestionUsuariosPage";
    }
}
