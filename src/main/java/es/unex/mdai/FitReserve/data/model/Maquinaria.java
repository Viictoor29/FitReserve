package es.unex.mdai.FitReserve.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Maquinaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMaquinaria;

    @NotNull
    @Column(unique = true, nullable = false, length = 20)
    private String nombre;

    @NotNull
    @Column(nullable = false)
    private int cantidadTotal;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoActividad tipoActividad;

    @Column(length = 255)
    private String descripcion;

    @OneToMany(mappedBy = "maquinaria",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnore
    private List<ReservaMaquinaria> reservas = new ArrayList<>();

    public Maquinaria() {}

    public Maquinaria(String nombre, int cantidadTotal, TipoActividad tipoActividad, String descripcion) {
        this.nombre = nombre;
        this.cantidadTotal = cantidadTotal;
        this.tipoActividad = tipoActividad;
        this.descripcion = descripcion;
    }

    public Maquinaria(String nombre, int cantidadTotal, TipoActividad tipoActividad) {
        this.nombre = nombre;
        this.cantidadTotal = cantidadTotal;
        this.tipoActividad = tipoActividad;
    }

    public Long getIdMaquinaria() {
        return idMaquinaria;
    }

    public void setIdMaquinaria(Long idMaquinaria) {
        this.idMaquinaria = idMaquinaria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(int cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public TipoActividad getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(TipoActividad tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<ReservaMaquinaria> getReservas() {
        return reservas;
    }

    public void setReservas(List<ReservaMaquinaria> reservas) {
        this.reservas = reservas;
    }
}
