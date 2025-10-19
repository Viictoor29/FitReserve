package es.unex.mdai.FitReserve.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@IdClass(ReservaMaquinariaId.class)
public class ReservaMaquinaria {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "idReserva", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Reserva reserva;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "idMaquinaria", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Maquinaria maquinaria;

    @NotNull
    @Column(nullable = false)
    private Integer cantidad;

    public ReservaMaquinaria() {}

    // Constructor de conveniencia con entidades
    public ReservaMaquinaria(Reserva reserva, Maquinaria maquinaria, Integer cantidad) {
        this.reserva = reserva;
        this.maquinaria = maquinaria;
        this.cantidad = cantidad;
    }

    // --- Getters/Setters ---
    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Maquinaria getMaquinaria() {
        return maquinaria;
    }

    public void setMaquinaria(Maquinaria maquinaria) {
        this.maquinaria = maquinaria;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    // Getters de IDs por comodidad (no mapean columnas)
    @Transient
    public Long getIdReserva() {
        return reserva != null ? reserva.getIdReserva() : null;
    }

    @Transient
    public Long getIdMaquinaria() {
        return maquinaria != null ? maquinaria.getIdMaquinaria() : null;
    }
}
