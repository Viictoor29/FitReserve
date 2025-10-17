package es.unex.mdai.FitReserve.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@IdClass(ReservaMaquinariaId.class)
public class ReservaMaquinaria {

    @Id
    @NotNull
    @Column(nullable = false)
    private long idReserva;

    @Id
    @NotNull
    @Column(nullable = false)
    private long idMaquinaria;

    @NotNull
    @Column(nullable = false)
    private int cantidad;

    public ReservaMaquinaria() {}

    public ReservaMaquinaria(long idReserva, long idMaquinaria, int cantidad) {
        this.idReserva = idReserva;
        this.idMaquinaria = idMaquinaria;
        this.cantidad = cantidad;
    }

    public long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(long idReserva) {
        this.idReserva = idReserva;
    }

    public long getIdMaquinaria() {
        return idMaquinaria;
    }

    public void setIdMaquinaria(long idMaquinaria) {
        this.idMaquinaria = idMaquinaria;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
