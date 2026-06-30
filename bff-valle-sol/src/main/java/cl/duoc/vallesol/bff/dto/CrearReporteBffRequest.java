package cl.duoc.vallesol.bff.dto;

import jakarta.validation.constraints.*;

/**
 * Datos que el frontend envia al BFF para registrar un reporte.
 * tipo y gravedad viajan como String (se validan formalmente aguas abajo en el MS).
 */
public class CrearReporteBffRequest {

    @NotBlank(message = "El nombre del ciudadano es obligatorio")
    private String nombreCiudadano;

    @NotBlank(message = "El tipo de emergencia es obligatorio")
    private String tipo;

    @NotBlank(message = "La descripcion es obligatoria")
    @Size(min = 10, max = 500)
    private String descripcion;

    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;

    private String sectorReferencia;

    @NotBlank(message = "La gravedad es obligatoria")
    private String gravedad;

    public String getNombreCiudadano() { return nombreCiudadano; }
    public void setNombreCiudadano(String nombreCiudadano) { this.nombreCiudadano = nombreCiudadano; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getSectorReferencia() { return sectorReferencia; }
    public void setSectorReferencia(String sectorReferencia) { this.sectorReferencia = sectorReferencia; }

    public String getGravedad() { return gravedad; }
    public void setGravedad(String gravedad) { this.gravedad = gravedad; }
}
