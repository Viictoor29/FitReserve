package es.unex.mdai.FitReserve.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Entrenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idEntrenador;

    @NotNull
    @Column(nullable = false, length = 50)
    private String especialidad;

    @NotNull
    @Column(nullable = false)
    private int experiencia;

    @NotNull
    @Column(nullable = false, length = 10)
    private String horaInicioTrabajo;

    @NotNull
    @Column(nullable = false, length = 10)
    private String horaFinTrabajo;

    public Entrenador() {}

    public Entrenador(String especialidad, int experiencia, String horaInicioTrabajo, String horaFinTrabajo) {
        this.especialidad = especialidad;
        this.experiencia = experiencia;
        this.horaInicioTrabajo = horaInicioTrabajo;
        this.horaFinTrabajo = horaFinTrabajo;
    }

    public long getIdEntrenador() {
        return idEntrenador;
    }

    public void setIdEntrenador(long idEntrenador) {
        this.idEntrenador = idEntrenador;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }

    public String getHoraInicioTrabajo() {
        return horaInicioTrabajo;
    }

    public void setHoraInicioTrabajo(String horaInicioTrabajo) {
        this.horaInicioTrabajo = horaInicioTrabajo;
    }

    public String getHoraFinTrabajo() {
        return horaFinTrabajo;
    }

    public void setHoraFinTrabajo(String horaFinTrabajo) {
        this.horaFinTrabajo = horaFinTrabajo;
    }
}
