package es.unex.mdai.FitReserve.data.repository;

import es.unex.mdai.FitReserve.data.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    // 1–1 con Usuario
    Optional<Cliente> findByIdCliente(Long idCliente);            // PK=FK a Usuario
    Optional<Cliente> findByUsuarioIdUsuario(Long idUsuario);     // por id de usuario

    List<Cliente> findAll();
}
