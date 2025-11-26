package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.enume.TipoUsuario;
import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.services.ClienteServicio;
import es.unex.mdai.FitReserve.services.EntrenadorServicio;
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
    private final ClienteServicio clienteServicio;
    private final EntrenadorServicio entrenadorServicio;

    public LoginController(UsuarioServicio usuarioServicio, ClienteServicio clienteServicio, EntrenadorServicio entrenadorServicio) {
        this.usuarioServicio = usuarioServicio;
        this.clienteServicio = clienteServicio;
        this.entrenadorServicio = entrenadorServicio;
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
            session.setAttribute("usuarioSesion", usuario);

            if(usuario.getTipoUsuario().equals(TipoUsuario.CLIENTE)) {
                return "clientePage";
            }else if (usuario.getTipoUsuario().equals(TipoUsuario.ENTRENADOR)) {
                return "entrenadorPage";
            }else {
                return "adminPage";
            }

        } catch (IllegalArgumentException e) {
            model.addAttribute("loginError", e.getMessage());
            return "login";
        } catch (Exception e) {
            model.addAttribute("loginError", "Error inesperado al iniciar sesión.");
            return "login";
        }
    }
}
