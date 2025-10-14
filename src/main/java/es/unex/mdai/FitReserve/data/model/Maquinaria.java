package es.unex.mdai.FitReserve.data.model;

import es.unex.mdai.FitReserve.data.enume.TipoActividad;
import es.unex.mdai.FitReserve.data.enume.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Maquinaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_maquinaria;

    @NotNull
    @Column(unique = true, nullable = false, length = 20)
    private String nombre;

    @NotNull
    @Column(nullable = false)
    int cantidad_total;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoActividad tipo_actividad;

    @Column(length = 255)
    private String descripcion;

    public long getId_maquinaria() {
        return id_maquinaria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad_total() {
        return cantidad_total;
    }

    public void setCantidad_total(int cantidad_total) {
        this.cantidad_total = cantidad_total;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
