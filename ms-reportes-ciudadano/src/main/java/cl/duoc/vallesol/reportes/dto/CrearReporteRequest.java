package cl.duoc.vallesol.reportes.dto;

import cl.duoc.vallesol.reportes.domain.Gravedad;
import cl.duoc.vallesol.reportes.domain.TipoEmergencia;
import jakarta.validation.constraints.*;

/**
 * Datos de entrada para crear un reporte. La validacion de formato (Bean Validation)
 * se ejecuta en el controlador; las reglas de negocio se validan en el servicio.
 */
public class CrearReporteRequest {

    @NotBlank(message = "El nombre del ciudadano es obligatorio")
    @Size(max = 120)
    private String nombreCiudadano;

    @NotNull(message = "El tipo de emergencia es obligatorio")
    private TipoEmergencia tipo;

    @NotBlank(message = "La descripcion es obligatoria")
    @Size(min = 10, max = 500, message = "La descripcion debe tener entre 10 y 500 caracteres")
    private String descripcion;

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "Latitud fuera de rango")
    @DecimalMax(value = "90.0", message = "Latitud fuera de rango")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "Longitud fuera de rango")
    @DecimalMax(value = "180.0", message = "Longitud fuera de rango")
    private Double longitud;

    @Size(max = 150)
    private String sectorReferencia;

    @NotNull(message = "La gravedad es obligatoria")
    private Gravedad gravedad;

    public String getNombreCiudadano() { return nombreCiudadano; }
    public void setNombreCiudadano(String nombreCiudadano) { this.nombreCiudadano = nombreCiudadano; }

    public TipoEmergencia getTipo() { return tipo; }
    public void setTipo(TipoEmergencia tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getSectorReferencia() { return sectorReferencia; }
    public void setSectorReferencia(String sectorReferencia) { this.sectorReferencia = sectorReferencia; }

    public Gravedad getGravedad() { return gravedad; }
    public void setGravedad(Gravedad gravedad) { this.gravedad = gravedad; }
}
