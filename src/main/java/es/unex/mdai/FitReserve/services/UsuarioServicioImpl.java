package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.data.repository.UsuarioRepository;

public class UsuarioServicioImpl implements UsuarioServicio {

    private final UsuarioRepository repository;

    public UsuarioServicioImpl(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario registrarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío.");
        }

        if (repository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado: " + usuario.getEmail());
        }

        return repository.save(usuario);
    }

    @Override
    public Usuario actualizarUsuario(Long idUsuario, Usuario datosActualizados) {

        if (idUsuario == null) {
            throw new IllegalArgumentException("El idUsuario no puede ser nulo.");
        }
        if (datosActualizados == null) {
            throw new IllegalArgumentException("Los datos actualizados no pueden ser nulos.");
        }

        Usuario existente = repository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe usuario con id: " + idUsuario));

        // Si el email cambia, comprobamos que no esté ya usado por otro usuario
        String nuevoEmail = datosActualizados.getEmail();
        if (nuevoEmail != null && !nuevoEmail.equals(existente.getEmail())) {
            if (repository.existsByEmail(nuevoEmail)) {
                throw new IllegalArgumentException("El email ya está registrado: " + nuevoEmail);
            }
            existente.setEmail(nuevoEmail);
        }

        // Actualizamos el resto de campos básicos (sin tocar id ni fechaCreacion)
        if (datosActualizados.getNombre() != null) {
            existente.setNombre(datosActualizados.getNombre());
        }
        if (datosActualizados.getApellidos() != null) {
            existente.setApellidos(datosActualizados.getApellidos());
        }
        if (datosActualizados.getContrasenia() != null) {
            existente.setContrasenia(datosActualizados.getContrasenia());
        }
        if (datosActualizados.getTipoUsuario() != null) {
            existente.setTipoUsuario(datosActualizados.getTipoUsuario());
        }
        if (datosActualizados.getTelefono() != null) {
            existente.setTelefono(datosActualizados.getTelefono());
        }

        return repository.save(existente);

    }

    @Override
    public void eliminarUsuario(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("El idUsuario no puede ser nulo.");
        }

        if (!repository.existsById(idUsuario)) {
            throw new IllegalArgumentException("No existe usuario con id: " + idUsuario);
        }

        repository.deleteByIdUsuario(idUsuario);
    }

    @Override
    public Usuario obtenerUsuarioPorId(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("El idUsuario no puede ser nulo.");
        }

        return repository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe usuario con id: " + idUsuario));
    }

    @Override
    public Usuario login(String email, String contrasenia) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío.");
        }
        if (contrasenia == null || contrasenia.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }

        return repository.findByEmailAndContrasenia(email, contrasenia)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas para email: " + email));
    }

    @Override
    public void cambiarContrasenia(Long idUsuario, String nuevaContrasenia) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("El idUsuario no puede ser nulo.");
        }
        if (nuevaContrasenia == null || nuevaContrasenia.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía.");
        }

        Usuario usuario = repository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe usuario con id: " + idUsuario));

        usuario.setContrasenia(nuevaContrasenia);
        repository.save(usuario);
    }

}
