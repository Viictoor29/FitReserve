package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.services.UsuarioServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.AuthProvider;

@Controller
public class LoginController {

    private final UsuarioServicio usuarioServicio;

    public LoginController(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping({"/login"})
    public String loginGet() {
        return "login";
    }

    @PostMapping({"/login"})
    public String loginPost(
            @RequestParam("email") String email,
            @RequestParam("contrasenia") String contrasenia,
            HttpSession session,
            Model model
    ) {

        try {
            Usuario usuario = usuarioServicio.login(email, contrasenia);

            // Guardar en sesión (ajustar nombre de getter si el modelo usa otro)
            session.setAttribute("usuarioId", usuario.getIdUsuario());
            session.setAttribute("usuario", usuario);

            // Redirigir al index/principal
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("loginError", e.getMessage());
            model.addAttribute("email", email);
            return "login";
        } catch (Exception e) {
            model.addAttribute("loginError", "Error inesperado al iniciar sesión.");
            model.addAttribute("email", email);
            return "login";
        }
    }
}
