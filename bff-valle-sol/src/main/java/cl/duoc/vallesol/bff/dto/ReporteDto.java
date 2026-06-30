package cl.duoc.vallesol.bff.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

/**
 * Representacion del reporte tal como lo expone el microservicio de Reportes.
 * gravedad/estado/tipo se modelan como String para desacoplar al BFF de los enums del MS.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReporteDto {

    private Long id;
    private String folio;
    private String nombreCiudadano;
    private String tipo;
    private String descripcion;
    private Double latitud;
    private Double longitud;
    private String sectorReferencia;
    private String gravedad;
    private String estado;
    private LocalDateTime fechaCreacion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }

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

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
