package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.enume.TipoUsuario;
import es.unex.mdai.FitReserve.data.model.Cliente;
import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.data.repository.ClienteRepository;
import es.unex.mdai.FitReserve.data.repository.UsuarioRepository;

import es.unex.mdai.FitReserve.services.ClienteServicio;
import es.unex.mdai.FitReserve.services.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistroController {
    
    private final UsuarioServicio usuarioServicio;
    private final ClienteServicio clienteServicio;

    @Autowired
    public RegistroController(UsuarioServicio usuarioServicio, ClienteServicio clienteServicio) {
        this.usuarioServicio = usuarioServicio;
        this.clienteServicio = clienteServicio;
    }


    @GetMapping("/registro")
    public String registroGet(Model model) {
        Cliente cliente = new Cliente();
        cliente.setUsuario(new Usuario());
        cliente.getUsuario().setTipoUsuario(TipoUsuario.CLIENTE);

        model.addAttribute("cliente", cliente);
        return "registro";
    }

    @PostMapping("/registro")
    public String registroPost(
            @Valid @ModelAttribute("cliente") Cliente cliente,
            BindingResult bindingResult,
            Model model
    ) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("regError", "El registro no ha funcionado");
            return "registro";
        }

        if(cliente == null || cliente.getUsuario() == null) {
            model.addAttribute("regError", "Datos de registro incompletos.");
            return "registro";
        }

        Usuario usuario = cliente.getUsuario();
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);

        try {
            // Primero guardamos el usuario (obtendremos idUsuario)
            Usuario usuarioGuardado = usuarioServicio.registrarUsuario(usuario);

            // Asignamos el usuario guardado (con id) al cliente
            cliente.setUsuario(usuarioGuardado);

            boolean creado = clienteServicio.registrarCliente(cliente);
            if (!creado) {
                model.addAttribute("regError", "Ya existe un cliente asociado a este usuario.");
                return "registro";
            }

            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("regError", ex.getMessage());
            return "registro";
        } catch (Exception ex) {
            model.addAttribute("regError", "Error inesperado durante el registro.");
            return "registro";
        }
    }


}
