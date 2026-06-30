// Modelos de datos compartidos entre el frontend y el BFF de Valle del Sol.

export type Gravedad = 'BAJA' | 'MEDIA' | 'ALTA' | 'CRITICA';

export interface Reporte {
  id: number;
  folio: string;
  nombreCiudadano: string;
  tipo: string;
  descripcion: string;
  latitud: number;
  longitud: number;
  sectorReferencia: string;
  gravedad: Gravedad;
  estado: string;
  fechaCreacion: string;
}

export interface Alerta {
  id: number;
  titulo: string;
  mensaje: string;
  nivel: 'INFORMATIVA' | 'PREVENTIVA' | 'EVACUACION';
  zona: string;
  reporteOrigenId: number;
  activa: boolean;
  fechaEmision: string;
}

// Cuerpo que el ciudadano envia al BFF para registrar un reporte.
export interface CrearReportePayload {
  nombreCiudadano: string;
  tipo: string;
  descripcion: string;
  latitud: number;
  longitud: number;
  sectorReferencia: string;
  gravedad: Gravedad;
}

// Respuesta del flujo de negocio: reporte creado + alerta si correspondio.
export interface ReporteRegistrado {
  reporte: Reporte;
  alertaGenerada: boolean;
  alerta: Alerta | null;
}

// Datos agregados para el Dashboard del Centro de Comando.
export interface DashboardData {
  totalReportes: number;
  reportesCriticos: number;
  alertasActivas: number;
  reportesPorGravedad: Record<string, number>;
  reportesPorEstado: Record<string, number>;
  ultimosReportes: Reporte[];
  alertasVigentes: Alerta[];
}
