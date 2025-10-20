package es.unex.mdai.FitReserve.data.repository;

import es.unex.mdai.FitReserve.data.enume.TipoUsuario;
import es.unex.mdai.FitReserve.data.model.Usuario;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario,Long> {

    Optional<Usuario> findByIdUsuario(Long idUsuario);

    Optional<Usuario> findByEmailAndContrasenia(String email, String contrasenia);

    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);

    // Por rol (admin/entrenador/cliente), si usas un enum -> cambia a TipoUsuario
    List<Usuario> findByTipoUsuario(TipoUsuario tipoUsuario);

    void deleteByIdUsuario(Long idUsuario);
}
