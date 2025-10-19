package es.unex.mdai.FitReserve.data.model;

import es.unex.mdai.FitReserve.data.enume.Genero;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
public class Cliente {

    @Id
    private long idCliente;

    @NotNull
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private Genero genero;

    @Column(length = 255)
    private String objetivos;

    @NotNull
    @OneToOne
    @MapsId
    @JoinColumn(name = "idCliente", referencedColumnName = "idUsuario")
    private Usuario usuario;


    public Cliente() {}

    public Cliente(LocalDate fechaNacimiento, Genero genero, String objetivos, Usuario usuario) {
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.objetivos = objetivos;
        this.usuario = usuario;
    }

    public Cliente(LocalDate fechaNacimiento, Genero genero, Usuario usuario) {
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.usuario = usuario;
    }

    public long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(long idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public String getObjetivos() {
        return objetivos;
    }

    public void setObjetivos(String objetivos) {
        this.objetivos = objetivos;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
