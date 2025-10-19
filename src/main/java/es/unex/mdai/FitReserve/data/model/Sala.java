package es.unex.mdai.FitReserve.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSala;

    @NotNull
    @Column(unique = true, nullable = false, length = 30)
    private String nombre;

    @NotNull
    @Column(nullable = false)
    private int capacidad;

    @NotNull
    @Column(nullable = false, length = 50)
    private String ubicacion;

    @Column(length = 255)
    private String descripcion;

    @OneToMany(mappedBy = "sala",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    public Sala() {}

    public Sala(String nombre, int capacidad, String ubicacion, String descripcion) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
    }

    public Sala(String nombre, int capacidad, String ubicacion) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.ubicacion = ubicacion;
    }

    public Long getIdSala() {
        return idSala;
    }

    public void setIdSala(Long idSala) {
        this.idSala = idSala;
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

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}
