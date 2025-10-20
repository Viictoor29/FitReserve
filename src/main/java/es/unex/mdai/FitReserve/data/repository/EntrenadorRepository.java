package es.unex.mdai.FitReserve.data.repository;

import es.unex.mdai.FitReserve.data.model.Actividad;
import es.unex.mdai.FitReserve.data.model.Entrenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntrenadorRepository extends JpaRepository<Entrenador,Long> {

}
