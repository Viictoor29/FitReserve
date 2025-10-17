package es.unex.mdai.FitReserve.data.model;

import java.io.Serializable;
import java.util.Objects;

public class ReservaMaquinariaId implements Serializable {

    private long idReserva;
    private long idMaquinaria;

    public ReservaMaquinariaId() {}

    public ReservaMaquinariaId(long idReserva, long idMaquinaria) {
        this.idReserva = idReserva;
        this.idMaquinaria = idMaquinaria;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReservaMaquinariaId that = (ReservaMaquinariaId) o;
        return idReserva == that.idReserva && idMaquinaria == that.idMaquinaria;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReserva, idMaquinaria);
    }
}
