package cl.duoc.vallesol.reportes.dto;

import cl.duoc.vallesol.reportes.domain.EstadoReporte;
import jakarta.validation.constraints.NotNull;

public class CambiarEstadoRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoReporte nuevoEstado;

    public EstadoReporte getNuevoEstado() { return nuevoEstado; }
    public void setNuevoEstado(EstadoReporte nuevoEstado) { this.nuevoEstado = nuevoEstado; }
}
