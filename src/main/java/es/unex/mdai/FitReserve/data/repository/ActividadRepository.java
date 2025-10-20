package es.unex.mdai.FitReserve.data.repository;

import es.unex.mdai.FitReserve.data.enume.NivelActividad;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad,Long> {

    Optional<Actividad> findByIdActividad(Long idActividad);
    
    List<Actividad> findByTipoActividad(TipoActividad tipoActividad);
    List<Actividad> findByNivel(NivelActividad nivel);

    List<Actividad> findAll();

    void deleteActividadsByIdActividad(Long idActividad);
}
