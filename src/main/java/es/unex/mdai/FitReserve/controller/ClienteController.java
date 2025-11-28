package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.services.UsuarioServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private UsuarioServicio usuarioService;

    @PostMapping("/eliminar-cuenta")
    public String eliminarCuenta(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");

        if (usuario != null) {
            usuarioService.eliminarUsuario(usuario.getIdUsuario());
            session.invalidate();
            return "redirect:/";
        }

        return "redirect:/login";
    }
}
