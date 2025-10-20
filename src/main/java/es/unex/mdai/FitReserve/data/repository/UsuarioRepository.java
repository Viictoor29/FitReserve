package es.unex.mdai.FitReserve.data.repository;

import es.unex.mdai.FitReserve.data.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario,Long> {

}
