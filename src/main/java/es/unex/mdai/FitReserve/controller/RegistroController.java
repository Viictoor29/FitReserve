package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.model.Cliente;
import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.data.repository.ClienteRepository;
import es.unex.mdai.FitReserve.data.repository.UsuarioRepository;

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

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/registro")
    public String registroGet(Model model) {
        Cliente cliente = new Cliente();
        cliente.setUsuario(new Usuario());

        model.addAttribute("cliente", cliente);
        return "registro";
    }

    @PostMapping("/registro")
    public String registroPost(
            @Valid @ModelAttribute("cliente") Cliente cliente,
            BindingResult bindingResult,
            Model model
    ) {

        if(cliente == null || cliente.getUsuario() == null) {
            model.addAttribute("regError", "Datos de registro incompletos.");
            return "registro";
        }

        Usuario usuario = cliente.getUsuario();





        return null;
    }


}
