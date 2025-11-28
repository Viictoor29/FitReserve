package es.unex.mdai.FitReserve.controller;

    import es.unex.mdai.FitReserve.data.enume.TipoUsuario;
    import es.unex.mdai.FitReserve.data.model.Cliente;
    import es.unex.mdai.FitReserve.data.model.Entrenador;
    import es.unex.mdai.FitReserve.data.model.Usuario;
    import es.unex.mdai.FitReserve.services.ClienteServicio;
    import es.unex.mdai.FitReserve.services.EntrenadorServicio;
    import es.unex.mdai.FitReserve.services.UsuarioServicio;
    import jakarta.validation.Valid;
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
            return "verCliente";
        }

        // FORMULARIO EDITAR CLIENTE
        @GetMapping("/clientes/editar/{id}")
        public String mostrarEditarCliente(@PathVariable Long id, Model model) {
            Cliente cliente = clienteServicio.obtenerClientePorId(id);
            model.addAttribute("clienteForm", cliente);
            return "editarCliente";
        }

        @PostMapping("/clientes/editar/{id}")
        public String procesarEditarCliente(
                @PathVariable Long id,
                @ModelAttribute("clienteForm") Cliente clienteForm,
                @RequestParam(required = false) String contrasenia,
                @RequestParam(required = false) String confirmContrasenia,
                Model model,
                RedirectAttributes redirectAttributes) {

            try {
                Cliente cliente = clienteServicio.obtenerClientePorId(id);
                if (cliente == null) {
                    redirectAttributes.addFlashAttribute("mensajeError", "Cliente no encontrado.");
                    return "redirect:/admin/usuarios";
                }

                // Validación de contraseñas
                if (contrasenia != null && !contrasenia.isBlank()) {
                    if (confirmContrasenia == null || !contrasenia.equals(confirmContrasenia)) {
                        model.addAttribute("mensajeError", "Las contraseñas no coinciden.");
                        model.addAttribute("clienteForm", clienteForm);
                        return "editarCliente";
                    }
                }

                // Validación de fecha de nacimiento si se proporciona
                if (clienteForm.getFechaNacimiento() != null &&
                        clienteForm.getFechaNacimiento().isAfter(java.time.LocalDate.now())) {
                    model.addAttribute("mensajeError", "La fecha de nacimiento no puede ser futura.");
                    model.addAttribute("clienteForm", clienteForm);
                    return "editarCliente";
                }

                // Actualizar datos de Usuario solo si se proporcionan
                Usuario usuario = cliente.getUsuario();

                if (clienteForm.getUsuario().getNombre() != null && !clienteForm.getUsuario().getNombre().isBlank()) {
                    usuario.setNombre(clienteForm.getUsuario().getNombre());
                }
                if (clienteForm.getUsuario().getApellidos() != null && !clienteForm.getUsuario().getApellidos().isBlank()) {
                    usuario.setApellidos(clienteForm.getUsuario().getApellidos());
                }
                if (clienteForm.getUsuario().getEmail() != null && !clienteForm.getUsuario().getEmail().isBlank()) {
                    usuario.setEmail(clienteForm.getUsuario().getEmail());
                }
                if (clienteForm.getUsuario().getTelefono() != null && !clienteForm.getUsuario().getTelefono().isBlank()) {
                    usuario.setTelefono(clienteForm.getUsuario().getTelefono());
                }
                if (contrasenia != null && !contrasenia.isBlank()) {
                    usuario.setContrasenia(contrasenia);
                }

                usuarioServicio.actualizarUsuario(usuario.getIdUsuario(), usuario);

                // Actualizar datos de Cliente solo si se proporcionan
                Cliente datosCliente = new Cliente();

                if (clienteForm.getFechaNacimiento() != null) {
                    datosCliente.setFechaNacimiento(clienteForm.getFechaNacimiento());
                }
                if (clienteForm.getGenero() != null) {
                    datosCliente.setGenero(clienteForm.getGenero());
                }
                if (clienteForm.getObjetivos() != null && !clienteForm.getObjetivos().isBlank()) {
                    datosCliente.setObjetivos(clienteForm.getObjetivos());
                }

                boolean clienteOk = clienteServicio.actualizarCliente(id, datosCliente);

                if (!clienteOk) {
                    redirectAttributes.addFlashAttribute("mensajeError", "No se pudo actualizar los datos del cliente.");
                    return "redirect:/admin/usuarios";
                }

                redirectAttributes.addFlashAttribute("mensajeExito", "Cliente actualizado correctamente.");

            } catch (IllegalArgumentException ex) {
                model.addAttribute("mensajeError", ex.getMessage());
                model.addAttribute("clienteForm", clienteForm);
                return "editarCliente";
            } catch (Exception e) {
                model.addAttribute("mensajeError", "Error al actualizar el cliente");
                model.addAttribute("clienteForm", clienteForm);
                return "editarCliente";
            }

            return "redirect:/admin/usuarios";
        }

        // ELIMINAR CLIENTE
        @PostMapping("/clientes/eliminar/{id}")
        public String eliminarCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
            try {
                Cliente cliente = clienteServicio.obtenerClientePorId(id);
                if (cliente == null) {
                    redirectAttributes.addFlashAttribute("mensajeError", "Cliente no encontrado.");
                    return "redirect:/admin/usuarios";
                }

                boolean ok = usuarioServicio.eliminarUsuario(cliente.getUsuario().getIdUsuario());

                if (ok) {
                    redirectAttributes.addFlashAttribute("mensajeExito", "Cliente eliminado correctamente.");
                } else {
                    redirectAttributes.addFlashAttribute("mensajeError", "No se ha podido eliminar el cliente.");
                }

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("mensajeError", "Error al eliminar el cliente: " + e.getMessage());
            }

            redirectAttributes.addFlashAttribute("timestamp", System.currentTimeMillis());
            return "redirect:/admin/usuarios";
        }

        /* ========== ENTRENADORES ========== */

        // VER ENTRENADOR
        @GetMapping("/entrenadores/{id}")
        public String verEntrenador(@PathVariable Long id, Model model) {
            Entrenador entrenador = entrenadorServicio.obtenerEntrenadorPorId(id);
            model.addAttribute("entrenador", entrenador);
            return "verEntrenador";
        }

        // FORMULARIO EDITAR ENTRENADOR
        @GetMapping("/entrenadores/editar/{id}")
        public String mostrarEditarEntrenador(@PathVariable Long id, Model model) {
            Entrenador entrenador = entrenadorServicio.obtenerEntrenadorPorId(id);
            model.addAttribute("entrenadorForm", entrenador);
            return "editarEntrenador";
        }

        @PostMapping("/entrenadores/editar/{id}")
        public String procesarEditarEntrenador(
                @PathVariable Long id,
                @ModelAttribute("entrenadorForm") Entrenador entrenadorForm,
                @RequestParam(required = false) String contrasenia,
                @RequestParam(required = false) String confirmContrasenia,
                Model model,
                RedirectAttributes redirectAttributes) {

            try {
                Entrenador entrenador = entrenadorServicio.obtenerEntrenadorPorId(id);
                if (entrenador == null) {
                    redirectAttributes.addFlashAttribute("mensajeError", "Entrenador no encontrado.");
                    return "redirect:/admin/usuarios";
                }

                // Validación de contraseñas
                if (contrasenia != null && !contrasenia.isBlank()) {
                    if (confirmContrasenia == null || !contrasenia.equals(confirmContrasenia)) {
                        model.addAttribute("mensajeError", "Las contraseñas no coinciden.");
                        model.addAttribute("entrenadorForm", entrenadorForm);
                        return "editarEntrenador";
                    }
                }

                // Validación de experiencia si se proporciona
                if (entrenadorForm.getExperiencia() > 0) {
                    if (entrenadorForm.getExperiencia() < 1 || entrenadorForm.getExperiencia() > 10) {
                        model.addAttribute("mensajeError", "La experiencia debe estar entre 1 y 10 años.");
                        model.addAttribute("entrenadorForm", entrenadorForm);
                        return "editarEntrenador";
                    }
                }

                // Validación de horas si ambas están proporcionadas
                if (entrenadorForm.getHoraInicioTrabajo() != null && entrenadorForm.getHoraFinTrabajo() != null) {
                    if (!entrenadorForm.getHoraFinTrabajo().isAfter(entrenadorForm.getHoraInicioTrabajo())) {
                        model.addAttribute("mensajeError", "La hora de fin debe ser posterior a la de inicio.");
                        model.addAttribute("entrenadorForm", entrenadorForm);
                        return "editarEntrenador";
                    }
                }

                // Actualizar datos de Usuario solo si se proporcionan
                Usuario usuario = entrenador.getUsuario();

                if (entrenadorForm.getUsuario().getNombre() != null && !entrenadorForm.getUsuario().getNombre().isBlank()) {
                    usuario.setNombre(entrenadorForm.getUsuario().getNombre());
                }
                if (entrenadorForm.getUsuario().getApellidos() != null && !entrenadorForm.getUsuario().getApellidos().isBlank()) {
                    usuario.setApellidos(entrenadorForm.getUsuario().getApellidos());
                }
                if (entrenadorForm.getUsuario().getEmail() != null && !entrenadorForm.getUsuario().getEmail().isBlank()) {
                    usuario.setEmail(entrenadorForm.getUsuario().getEmail());
                }
                if (entrenadorForm.getUsuario().getTelefono() != null && !entrenadorForm.getUsuario().getTelefono().isBlank()) {
                    usuario.setTelefono(entrenadorForm.getUsuario().getTelefono());
                }
                if (contrasenia != null && !contrasenia.isBlank()) {
                    usuario.setContrasenia(contrasenia);
                }

                usuarioServicio.actualizarUsuario(usuario.getIdUsuario(), usuario);

                // Actualizar datos de Entrenador solo si se proporcionan
                Entrenador datosEntrenador = new Entrenador();

                if (entrenadorForm.getEspecialidad() != null && !entrenadorForm.getEspecialidad().isBlank()) {
                    datosEntrenador.setEspecialidad(entrenadorForm.getEspecialidad());
                }
                if (entrenadorForm.getExperiencia() > 0) {
                    datosEntrenador.setExperiencia(entrenadorForm.getExperiencia());
                }
                if (entrenadorForm.getHoraInicioTrabajo() != null) {
                    datosEntrenador.setHoraInicioTrabajo(entrenadorForm.getHoraInicioTrabajo());
                }
                if (entrenadorForm.getHoraFinTrabajo() != null) {
                    datosEntrenador.setHoraFinTrabajo(entrenadorForm.getHoraFinTrabajo());
                }

                boolean entrenadorOk = entrenadorServicio.actualizarEntrenador(id, datosEntrenador);

                if (!entrenadorOk) {
                    redirectAttributes.addFlashAttribute("mensajeError", "No se pudo actualizar los datos del entrenador.");
                    return "redirect:/admin/usuarios";
                }

                redirectAttributes.addFlashAttribute("mensajeExito", "Entrenador actualizado correctamente.");

            } catch (IllegalArgumentException ex) {
                model.addAttribute("mensajeError", ex.getMessage());
                model.addAttribute("entrenadorForm", entrenadorForm);
                return "editarEntrenador";
            } catch (Exception e) {
                model.addAttribute("mensajeError", "Error al actualizar el entrenador");
                model.addAttribute("entrenadorForm", entrenadorForm);
                return "editarEntrenador";
            }

            return "redirect:/admin/usuarios";
        }

        // ELIMINAR ENTRENADOR
        @PostMapping("/entrenadores/eliminar/{id}")
        public String eliminarEntrenador(@PathVariable Long id, RedirectAttributes redirectAttributes) {
            try {
                Entrenador entrenador = entrenadorServicio.obtenerEntrenadorPorId(id);
                if (entrenador == null) {
                    redirectAttributes.addFlashAttribute("mensajeError", "Entrenador no encontrado.");
                    return "redirect:/admin/usuarios";
                }

                boolean ok = usuarioServicio.eliminarUsuario(entrenador.getUsuario().getIdUsuario());

                if (ok) {
                    redirectAttributes.addFlashAttribute("mensajeExito", "Entrenador eliminado correctamente.");
                } else {
                    redirectAttributes.addFlashAttribute("mensajeError", "No se ha podido eliminar el entrenador.");
                }

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("mensajeError", "Error al eliminar el entrenador: " + e.getMessage());
            }

            redirectAttributes.addFlashAttribute("timestamp", System.currentTimeMillis());
            return "redirect:/admin/usuarios";
        }

        /* ==================== NUEVO CLIENTE ==================== */

        @GetMapping("/clientes/nuevo")
        public String mostrarNuevoCliente(Model model) {
            Cliente cliente = new Cliente();
            cliente.setUsuario(new Usuario());
            cliente.getUsuario().setTipoUsuario(TipoUsuario.CLIENTE);

            model.addAttribute("clienteForm", cliente);
            return "nuevoCliente";
        }

        @PostMapping("/clientes/nuevo")
        public String procesarNuevoCliente(
                @Valid @ModelAttribute("clienteForm") Cliente clienteForm,  // Cambiado de "cliente" a "clienteForm"
                BindingResult bindingResult,
                Model model,
                RedirectAttributes redirectAttributes) {

            if (bindingResult.hasErrors()) {
                model.addAttribute("regError", "El registro no ha funcionado");
                return "nuevoCliente";
            }

            if (clienteForm == null || clienteForm.getUsuario() == null) {
                model.addAttribute("regError", "Datos de registro incompletos.");
                return "nuevoCliente";
            }

            Usuario usuario = clienteForm.getUsuario();
            usuario.setTipoUsuario(TipoUsuario.CLIENTE);

            try {
                Usuario usuarioGuardado = usuarioServicio.registrarUsuario(usuario);
                clienteForm.setUsuario(usuarioGuardado);

                boolean creado = clienteServicio.registrarCliente(clienteForm);
                if (!creado) {
                    model.addAttribute("regError", "Ya existe un cliente asociado a este usuario.");
                    return "nuevoCliente";
                }

                redirectAttributes.addFlashAttribute("mensajeExito", "Cliente registrado correctamente.");
                return "redirect:/admin/usuarios";

            } catch (IllegalArgumentException ex) {
                model.addAttribute("regError", ex.getMessage());
                return "nuevoCliente";
            } catch (Exception ex) {
                model.addAttribute("regError", "Error inesperado durante el registro.");
                return "nuevoCliente";
            }
        }

        /* ==================== NUEVO ENTRENADOR ==================== */

        @GetMapping("/entrenadores/nuevo")
        public String mostrarNuevoEntrenador(Model model) {
            Entrenador entrenador = new Entrenador();
            entrenador.setUsuario(new Usuario());
            entrenador.getUsuario().setTipoUsuario(TipoUsuario.ENTRENADOR);

            model.addAttribute("entrenador", entrenador);
            return "nuevoEntrenador";
        }

        @PostMapping("/entrenadores/nuevo")
        public String procesarNuevoEntrenador(
                @Valid @ModelAttribute("entrenador") Entrenador entrenador,
                BindingResult bindingResult,
                Model model,
                RedirectAttributes redirectAttributes) {

            if (bindingResult.hasErrors()) {
                model.addAttribute("regError", "El registro no ha funcionado");
                return "nuevoEntrenador";
            }

            if (entrenador == null || entrenador.getUsuario() == null) {
                model.addAttribute("regError", "Datos de registro incompletos.");
                return "nuevoEntrenador";
            }

            Usuario usuario = entrenador.getUsuario();
            usuario.setTipoUsuario(TipoUsuario.ENTRENADOR);

            try {
                Usuario usuarioGuardado = usuarioServicio.registrarUsuario(usuario);
                entrenador.setUsuario(usuarioGuardado);

                boolean creado = entrenadorServicio.registrarEntrenador(entrenador);
                if (!creado) {
                    model.addAttribute("regError", "Ya existe un entrenador asociado a este usuario.");
                    return "nuevoEntrenador";
                }

                redirectAttributes.addFlashAttribute("mensajeExito", "Entrenador registrado correctamente.");
                return "redirect:/admin/usuarios";

            } catch (IllegalArgumentException ex) {
                model.addAttribute("regError", ex.getMessage());
                return "nuevoEntrenador";
            } catch (Exception ex) {
                model.addAttribute("regError", "Error inesperado durante el registro.");
                return "nuevoEntrenador";
            }
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