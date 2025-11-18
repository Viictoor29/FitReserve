package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.model.Usuario;

public interface UsuarioServicio {

    Usuario registrarUsuario(Usuario usuario);
    Usuario login(String email, String contrasenia);
    void cambiarContrasenia(Long idUsuario, String nuevaContrasenia);
    void eliminarUsuario(Long idUsuario);
}
