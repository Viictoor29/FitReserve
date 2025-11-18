package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.model.Usuario;

public interface UsuarioServicio {

    // --- CRUD básico ---
    Usuario registrarUsuario(Usuario usuario);

    Usuario actualizarUsuario(Long idUsuario, Usuario datosActualizados);

    void eliminarUsuario(Long idUsuario);

    Usuario obtenerUsuarioPorId(Long idUsuario);

    // --- Login ---
    /**
     * Autentica al usuario con email y contraseña.
     * Devuelve el usuario completo si las credenciales son válidas.
     */
    Usuario login(String email, String contrasenia);

    // --- Gestión de seguridad ---
    void cambiarContrasenia(Long idUsuario, String nuevaContrasenia);

    // Opcional pero muy útil:
    boolean existeEmail(String email);
}
