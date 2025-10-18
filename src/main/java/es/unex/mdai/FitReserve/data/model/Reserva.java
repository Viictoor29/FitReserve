package es.unex.mdai.FitReserve.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idReserva;

    private long idCliente;

    private long idSala;

    private long idActividad;

    private long idEntrenador;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaHoraInicio;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaHoraFin;

    @NotNull
    @Column(nullable = false, length = 20)
    private String estado;

    @Column(length = 255)
    private String comentarios;

    public Reserva() {}

    public Reserva(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, String estado, String comentarios) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = estado;
        this.comentarios = comentarios;
    }

    public long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(long idReserva) {
        this.idReserva = idReserva;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }
}
