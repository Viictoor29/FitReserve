package es.unex.mdai.FitReserve.data.model;

import es.unex.mdai.FitReserve.data.enume.Estado;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaHoraInicio;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaHoraFin;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    @Column(length = 255)
    private String comentarios;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idCliente", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idEntrenador", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Entrenador entrenador;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idActividad", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Actividad actividad;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idSala", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Sala sala;

    @OneToMany(mappedBy = "reserva",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ReservaMaquinaria> maquinariaAsignada = new ArrayList<>();

    public Reserva() {}

    public Reserva(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, Estado estado, String comentarios) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = estado;
        this.comentarios = comentarios;
    }

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
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

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Entrenador getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(Entrenador entrenador) {
        this.entrenador = entrenador;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public List<ReservaMaquinaria> getMaquinariaAsignada() {
        return maquinariaAsignada;
    }

    public void setMaquinariaAsignada(List<ReservaMaquinaria> maquinariaAsignada) {
        this.maquinariaAsignada = maquinariaAsignada;
    }
}
