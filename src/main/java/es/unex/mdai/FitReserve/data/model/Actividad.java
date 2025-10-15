package es.unex.mdai.FitReserve.data.model;

import es.unex.mdai.FitReserve.data.enume.NivelActividad;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_actividad;

    @NotNull
    @Column(nullable = false, length = 20, unique = true)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    //Crear enum para el tipo de actividad
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoActividad tipo_actividad;

    //Crear enum para la nivel
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelActividad nivel;

}

