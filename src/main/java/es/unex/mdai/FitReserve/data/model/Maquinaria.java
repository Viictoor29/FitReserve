package es.unex.mdai.FitReserve.data.model;

import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.enume.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Maquinaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idMaquinaria;

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

    public long getIdMaquinaria() {
        return idMaquinaria;
    }

    public void setIdMaquinaria(long idMaquinaria) {
        this.idMaquinaria = idMaquinaria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoActividad getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(TipoActividad tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    public int getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(int cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
