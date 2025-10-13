package es.unex.mdai.FitReserve.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_sala;

    @NotNull
    @Column(unique = true, nullable = false, length = 30)
    String nombre;

    @NotNull
    @Column(nullable = false)
    private int capacidad;

    @NotNull
    @Column(nullable = false, length = 50)
    private String ubicacion;

    @Column(length = 255)
    private String descripcion;

    public long getId_sala() {
        return id_sala;
    }

    public void setId_sala(long id_sala) {
        this.id_sala = id_sala;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
