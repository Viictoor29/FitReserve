package es.unex.mdai.FitReserve.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Entity
public class Entrenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idEntrenador;

    @NotNull
    @Column(nullable = false, length = 50)
    private String especialidad;

    @NotNull
    @Min(1)
    @Max(10)
    @Column(nullable = false)
    private int experiencia;

    @NotNull
    @JsonFormat(pattern = "HH:mm")
    @Column(nullable = false, length = 10)
    private LocalTime horaInicioTrabajo;

    @NotNull
    @JsonFormat(pattern = "HH:mm")
    @Column(nullable = false, length = 10)
    private LocalTime horaFinTrabajo;

    public Entrenador() {}

    public Entrenador(String especialidad, int experiencia, LocalTime horaInicioTrabajo, LocalTime horaFinTrabajo) {
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

    public LocalTime getHoraInicioTrabajo() {
        return horaInicioTrabajo;
    }

    public void setHoraInicioTrabajo(LocalTime horaInicioTrabajo) {
        this.horaInicioTrabajo = horaInicioTrabajo;
    }

    public LocalTime getHoraFinTrabajo() {
        return horaFinTrabajo;
    }

    public void setHoraFinTrabajo(LocalTime horaFinTrabajo) {
        this.horaFinTrabajo = horaFinTrabajo;
    }
}
