import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CrearReportePayload,
  DashboardData,
  Reporte,
  ReporteRegistrado,
  Alerta
} from '../models/reporte.interface';

/**
 * Servicio que comunica el frontend con el BFF de Valle del Sol.
 * El BFF (puerto 8080) es la unica API que consume el frontend; el orquesta
 * la comunicacion con los microservicios de Reportes y Alertas.
 */
@Injectable({ providedIn: 'root' })
export class ReportesService {

  // URL base del BFF. Cambiar por la URL de produccion al desplegar.
  private readonly bffUrl = 'http://localhost:8080/api/bff';

  constructor(private http: HttpClient) {}

  /** Registra un reporte ciudadano (dispara alerta si es critico). */
  crearReporte(payload: CrearReportePayload): Observable<ReporteRegistrado> {
    return this.http.post<ReporteRegistrado>(`${this.bffUrl}/reportes`, payload);
  }

  /** Lista todos los reportes. */
  listarReportes(): Observable<Reporte[]> {
    return this.http.get<Reporte[]>(`${this.bffUrl}/reportes`);
  }

  /** Lista las alertas comunitarias activas. */
  listarAlertas(): Observable<Alerta[]> {
    return this.http.get<Alerta[]>(`${this.bffUrl}/alertas`);
  }

  /** Datos agregados para el Dashboard del Centro de Comando. */
  obtenerDashboard(): Observable<DashboardData> {
    return this.http.get<DashboardData>(`${this.bffUrl}/dashboard`);
  }
}
