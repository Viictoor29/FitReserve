package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectUsrController {

    @GetMapping("/admin")
    public String adminPage(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");

        if (usuario == null) {
            return "redirect:/login";
        }

        return "adminPage";
    }

    @GetMapping("/entrenador")
    public String entrenadorPage(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");

        if (usuario == null) {
            return "redirect:/login";
        }

        return "entrenadorPage";
    }

    @GetMapping("/cliente")
    public String clientePage(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");

        if (usuario == null) {
            return "redirect:/login";
        }

        return "clientePage";
    }
}
