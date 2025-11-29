package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.enume.Estado;
import es.unex.mdai.FitReserve.data.model.Entrenador;
import es.unex.mdai.FitReserve.data.model.Reserva;
import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.services.EntrenadorServicio;
import es.unex.mdai.FitReserve.services.ReservaServicio;
import es.unex.mdai.FitReserve.services.UsuarioServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/entrenador")
public class EntrenadorController {

    private final EntrenadorServicio entrenadorService;
    private final UsuarioServicio usuarioService;
    @Autowired
    private ReservaServicio reservaService;

    public EntrenadorController(EntrenadorServicio entrenadorService, UsuarioServicio usuarioService) {
        this.entrenadorService = entrenadorService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");

        if (usuario == null) {
            return "redirect:/login";
        }

        Entrenador entrenador = entrenadorService.obtenerPorIdUsuario(usuario.getIdUsuario());
        model.addAttribute("usuario", usuario);
        model.addAttribute("entrenador", entrenador);

        return "perfilEntrenador";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam(required = false) String contrasenia,
            @RequestParam(required = false) String confirmContrasenia,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            Usuario datosUsuario = new Usuario();
            datosUsuario.setNombre(nombre);
            datosUsuario.setApellidos(apellidos);
            datosUsuario.setEmail(email);
            datosUsuario.setTelefono(telefono);

            if (contrasenia != null && !contrasenia.isBlank()) {
                datosUsuario.setContrasenia(contrasenia);
            }

            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario.getIdUsuario(), datosUsuario);

            session.setAttribute("usuarioSesion", usuarioActualizado);
            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil");
        }

        return "redirect:/entrenador/perfil";
    }

    @GetMapping("/historial")
    public String mostrarHistorial(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) {
            return "redirect:/login";
        }

        Entrenador entrenador = entrenadorService.obtenerPorIdUsuario(usuario.getIdUsuario());
        if (entrenador == null) {
            model.addAttribute("error", "No se encontró el entrenador asociado al usuario.");
            return "entrenadorPage";
        }

        List<Reserva> historial = reservaService.listarHistorialEntrenador(entrenador.getIdEntrenador());

        List<Reserva> completadas = historial.stream()
                .filter(r -> r.getEstado() == Estado.Completada)
                .collect(Collectors.toList());

        List<Reserva> canceladas = historial.stream()
                .filter(r -> r.getEstado() == Estado.Cancelada)
                .collect(Collectors.toList());

        model.addAttribute("historialCompletadas", completadas);
        model.addAttribute("historialCanceladas", canceladas);
        model.addAttribute("usuario", usuario);

        return "historialReservaEntrenador";
    }
}