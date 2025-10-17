package es.unex.mdai.FitReserve.data.model;

import es.unex.mdai.FitReserve.data.enume.NivelActividad;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idActividad;

    @NotNull
    @Column(nullable = false, length = 50, unique = true)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    //Crear enum para el tipo de actividad
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoActividad tipoActividad;

    //Crear enum para la nivel
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelActividad nivel;

    public Actividad() {}

    public Actividad(String nombre, String descripcion, TipoActividad tipoActividad, NivelActividad nivel) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoActividad = tipoActividad;
        this.nivel = nivel;
    }

    public Actividad(String nombre, TipoActividad tipoActividad, NivelActividad nivel) {
        this.nombre = nombre;
        this.tipoActividad = tipoActividad;
        this.nivel = nivel;
    }

    public long getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(long idActividad) {
        this.idActividad = idActividad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoActividad getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(TipoActividad tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    public NivelActividad getNivel() {
        return nivel;
    }

    public void setNivel(NivelActividad nivel) {
        this.nivel = nivel;
    }
}

