package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.model.Cliente;
import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.services.UsuarioServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import es.unex.mdai.FitReserve.data.enume.Genero;
import es.unex.mdai.FitReserve.services.ClienteServicio;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private UsuarioServicio usuarioService;

    @Autowired
    private ClienteServicio clienteService;

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

    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");

        if (usuario == null) {
            return "redirect:/login";
        }

        Cliente cliente = clienteService.obtenerPorIdUsuario(usuario.getIdUsuario());
        model.addAttribute("usuario", usuario);
        model.addAttribute("cliente", cliente);

        return "perfilCliente";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String fechaNacimiento,
            @RequestParam String genero,
            @RequestParam(required = false) String objetivos,
            @RequestParam(required = false) String contrasenia,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            // Preparar y actualizar datos de Usuario -> recibimos el Usuario actualizado
            Usuario datosUsuario = new Usuario();
            datosUsuario.setNombre(nombre);
            datosUsuario.setApellidos(apellidos);
            datosUsuario.setEmail(email);
            datosUsuario.setTelefono(telefono);
            if (contrasenia != null && !contrasenia.isBlank()) {
                datosUsuario.setContrasenia(contrasenia);
            }

            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario.getIdUsuario(), datosUsuario);

            // Preparar y actualizar datos de Cliente -> recibimos booleano de éxito
            Cliente datosCliente = new Cliente();
            datosCliente.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            datosCliente.setGenero(Genero.valueOf(genero));
            datosCliente.setObjetivos(objetivos);

            boolean clienteOk = clienteService.actualizarCliente(usuario.getIdUsuario(), datosCliente);
            if (!clienteOk) {
                redirectAttributes.addFlashAttribute("error", "No se pudo actualizar los datos del cliente.");
                // Actualizar la sesión con lo que tengamos del usuario actualizado (si procede)
                session.setAttribute("usuarioSesion", usuarioActualizado);
                return "redirect:/cliente/perfil";
            }

            // Actualizar sesión con el usuario devuelto por el servicio
            session.setAttribute("usuarioSesion", usuarioActualizado);
            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }

        return "redirect:/cliente/perfil";
    }
}
