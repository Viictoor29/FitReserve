package es.unex.mdai.FitReserve.data.model;

import java.io.Serializable;
import java.util.Objects;

public class ReservaMaquinariaId implements Serializable {

    // Los nombres deben coincidir con las propiedades @Id de la entidad:
    private Long reserva;
    private Long maquinaria;

    public ReservaMaquinariaId() {}

    public ReservaMaquinariaId(Long reserva, Long maquinaria) {
        this.reserva = reserva;
        this.maquinaria = maquinaria;
    }

    public Long getReserva() { return reserva; }
    public void setReserva(Long reserva) { this.reserva = reserva; }

    public Long getMaquinaria() { return maquinaria; }
    public void setMaquinaria(Long maquinaria) { this.maquinaria = maquinaria; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservaMaquinariaId that = (ReservaMaquinariaId) o;
        return Objects.equals(reserva, that.reserva) &&
                Objects.equals(maquinaria, that.maquinaria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reserva, maquinaria);
    }
}
