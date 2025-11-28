package es.unex.mdai.FitReserve.controller;

import es.unex.mdai.FitReserve.data.enume.Genero;
import es.unex.mdai.FitReserve.data.enume.TipoUsuario;
import es.unex.mdai.FitReserve.data.model.Cliente;
import es.unex.mdai.FitReserve.data.model.Entrenador;
import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.services.ClienteServicio;
import es.unex.mdai.FitReserve.services.EntrenadorServicio;
import es.unex.mdai.FitReserve.services.UsuarioServicio;
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
    private final UsuarioServicio usuarioServicio;

    public GestionUsuariosController(ClienteServicio clienteServicio,
                                     EntrenadorServicio entrenadorServicio,
                                     UsuarioServicio usuarioServicio) {
        this.clienteServicio = clienteServicio;
        this.entrenadorServicio = entrenadorServicio;
        this.usuarioServicio = usuarioServicio;
    }

    /* ========== LISTA PRINCIPAL ========== */

    @GetMapping
    public String mostrarGestionUsuarios(Model model) {
        cargarListas(model);
        return "gestionUsuariosPage";
    }

    /* ========== CLIENTES ========== */

    // VER CLIENTE
    @GetMapping("/clientes/{id}")
    public String verCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteServicio.obtenerClientePorId(id);
        model.addAttribute("cliente", cliente);
        return "verCliente"; // crea esta vista
    }

    // FORMULARIO EDITAR CLIENTE
    @GetMapping("/clientes/editar/{id}")
    public String mostrarEditarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteServicio.obtenerClientePorId(id);
        model.addAttribute("clienteForm", cliente);
        return "editarCliente"; // crea esta vista
    }

    // PROCESAR EDITAR CLIENTE
    @PostMapping("/clientes/editar/{id}")
    public String procesarEditarCliente(@PathVariable Long id,
                                        @ModelAttribute("clienteForm") Cliente clienteForm,
                                        BindingResult result,
                                        RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "editarCliente";
        }

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
        return "verEntrenador"; // crea esta vista
    }

    // FORMULARIO EDITAR ENTRENADOR
    @GetMapping("/entrenadores/editar/{id}")
    public String mostrarEditarEntrenador(@PathVariable Long id, Model model) {
        Entrenador entrenador = entrenadorServicio.obtenerEntrenadorPorId(id);
        model.addAttribute("entrenadorForm", entrenador);
        return "editarEntrenador"; // crea esta vista
    }

    // PROCESAR EDITAR ENTRENADOR
    @PostMapping("/entrenadores/editar/{id}")
    public String procesarEditarEntrenador(@PathVariable Long id,
                                           @ModelAttribute("entrenadorForm") Entrenador entrenadorForm,
                                           BindingResult result,
                                           RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "editarEntrenador";
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


    /* ==================== NUEVO CLIENTE ==================== */

    // Mostrar formulario
    @GetMapping("/clientes/nuevo")
    public String mostrarNuevoCliente(Model model) {
        Cliente cliente = new Cliente();
        cliente.setUsuario(new Usuario()); // para poder hacer usuario.nombre, etc. en Thymeleaf

        model.addAttribute("clienteForm", cliente);
        model.addAttribute("generos", Genero.values());

        return "nuevoCliente";
    }

    // Procesar envío del formulario
    @PostMapping("/clientes/nuevo")
    public String procesarNuevoCliente(
            @ModelAttribute("clienteForm") Cliente clienteForm,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "nuevoCliente";
        }

        Usuario usuario = clienteForm.getUsuario();
        // marcamos tipo de usuario como CLIENTE
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);

        // 1) Guardar usuario (para que tenga idUsuario)
        // (ajusta el nombre del método si en tu UsuarioServicio se llama distinto)
        usuarioServicio.registrarUsuario(usuario);

        // 2) Registrar cliente; por @MapsId usará usuario.idUsuario
        boolean ok = clienteServicio.registrarCliente(clienteForm);

        if (!ok) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se ha podido registrar el cliente (puede que ya exista).");
            return "redirect:/admin/usuarios";
        }

        redirectAttributes.addFlashAttribute("mensajeExito",
                "Cliente registrado correctamente.");
        return "redirect:/admin/usuarios";
    }

    /* ==================== NUEVO ENTRENADOR ==================== */

    // Mostrar formulario
    @GetMapping("/entrenadores/nuevo")
    public String mostrarNuevoEntrenador(Model model) {
        Entrenador entrenador = new Entrenador();
        entrenador.setUsuario(new Usuario());

        model.addAttribute("entrenadorForm", entrenador);
        return "nuevoEntrenador";
    }

    // Procesar envío del formulario
    @PostMapping("/entrenadores/nuevo")
    public String procesarNuevoEntrenador(
            @ModelAttribute("entrenadorForm") Entrenador entrenadorForm,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "nuevoEntrenador";
        }

        Usuario usuario = entrenadorForm.getUsuario();
        usuario.setTipoUsuario(TipoUsuario.ENTRENADOR);

        // 1) Guardar usuario
        usuarioServicio.registrarUsuario(usuario);

        // 2) Registrar entrenador
        boolean ok = entrenadorServicio.registrarEntrenador(entrenadorForm);

        if (!ok) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se ha podido registrar el entrenador (puede que ya exista).");
            return "redirect:/admin/usuarios";
        }

        redirectAttributes.addFlashAttribute("mensajeExito",
                "Entrenador registrado correctamente.");
        return "redirect:/admin/usuarios";
    }

    private void cargarListas(Model model) {
        var clientes = clienteServicio.listarTodos().stream()
                .filter(c -> c.getUsuario() != null
                        && c.getUsuario().getTipoUsuario() == TipoUsuario.CLIENTE)
                .toList();

        var entrenadores = entrenadorServicio.listarTodos().stream()
                .filter(e -> e.getUsuario() != null
                        && e.getUsuario().getTipoUsuario() == TipoUsuario.ENTRENADOR)
                .toList();

        model.addAttribute("clientes", clientes);
        model.addAttribute("entrenadores", entrenadores);
    }
}
