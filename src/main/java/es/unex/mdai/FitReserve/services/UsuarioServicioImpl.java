package es.unex.mdai.FitReserve.services;

import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.data.repository.UsuarioRepository;

public class UsuarioServicioImpl implements UsuarioServicio {

    private UsuarioRepository repository;

    public UsuarioServicioImpl() {}

    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        if(usuario==null) throw new IllegalArgumentException("El usuario no puede ser nulo.");

        if(repository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado: " + usuario.getEmail());
        }
        return null;
        
    }

    @Override
    public Usuario actualizarUsuario(Long idUsuario, Usuario datosActualizados) {
        return null;
    }

    @Override
    public void eliminarUsuario(Long idUsuario) {

    }

    @Override
    public Usuario obtenerUsuarioPorId(Long idUsuario) {
        return null;
    }

    @Override
    public Usuario login(String email, String contrasenia) {
        return null;
    }

    @Override
    public void cambiarContrasenia(Long idUsuario, String nuevaContrasenia) {

    }

}
