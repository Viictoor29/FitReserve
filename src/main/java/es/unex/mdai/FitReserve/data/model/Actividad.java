package es.unex.mdai.FitReserve.data.model;

import es.unex.mdai.FitReserve.data.enume.NivelActividad;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idActividad;

    @NotNull
    @Column(nullable = false, length = 50, unique = true)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoActividad tipoActividad;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelActividad nivel;

    @OneToMany(mappedBy = "actividad",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

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

    public Long getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(Long idActividad) {
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

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}
