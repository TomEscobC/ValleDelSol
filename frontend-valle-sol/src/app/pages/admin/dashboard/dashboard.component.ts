import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { timeout } from 'rxjs';
import { ReportesService } from '../../../core/services/reportes.service';
import { DashboardData, Reporte, Alerta } from '../../../core/models/reporte.interface';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './dashboard.component.html',
    styles: [`
    .fade-in { animation: fadeIn 0.4s ease-in; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

    .glass-card-pro { background: rgba(15, 23, 42, 0.4); backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px); border: 1px solid rgba(16, 185, 129, 0.15); border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,0.5); }
    .neon-shadow { box-shadow: 0 0 15px rgba(16, 185, 129, 0.4); }
    .text-emerald-neon { color: #34d399; text-shadow: 0 0 8px rgba(52, 211, 153, 0.5); }

    .kpi-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; margin-bottom: 24px; }

    .primeng-table { width: 100%; border-collapse: collapse; text-align: left; }
    .primeng-table th { background: rgba(0,0,0,0.4); color: #94a3b8; font-size: 0.75rem; text-transform: uppercase; padding: 12px 16px; border-bottom: 1px solid rgba(16,185,129,0.2); }
    .primeng-table td { padding: 14px 16px; border-bottom: 1px solid rgba(255,255,255,0.05); font-size: 0.85rem; color: #cbd5e1; }
    .primeng-table tr:hover { background: rgba(16,185,129,0.05); }

    .badge { display:inline-block; padding:4px 8px; border-radius:12px; font-size:0.7rem; font-weight:700; border:1px solid; }
    .b-crit { background:rgba(239,68,68,0.1); color:#fca5a5; border-color:rgba(239,68,68,0.3); }
    .b-alta { background:rgba(234,179,8,0.1); color:#fde047; border-color:rgba(234,179,8,0.3); }
    .b-norm { background:rgba(16,185,129,0.1); color:#34d399; border-color:rgba(16,185,129,0.3); }

    .evac-banner { border:1px solid rgba(239,68,68,0.5); background:rgba(239,68,68,0.08); border-radius:14px; padding:14px 18px; margin-bottom:22px; display:flex; align-items:center; gap:12px; }
    .offline-note { color:#fbbf24; font-size:0.78rem; background:rgba(251,191,36,0.08); border:1px solid rgba(251,191,36,0.25); padding:6px 12px; border-radius:8px; display:inline-block; margin-bottom:16px; }
  `]
})
export class DashboardComponent implements OnInit {

    data: DashboardData | null = null;
    cargando = true;
    offline = false;

    // Datos demo de respaldo: el panel siempre muestra algo aunque el BFF no responda.
    private readonly demo: DashboardData = {
        totalReportes: 4, reportesCriticos: 3, alertasActivas: 1,
        reportesPorGravedad: { CRITICA: 1, ALTA: 2, MEDIA: 1 },
        reportesPorEstado: { PENDIENTE: 2, VALIDADO: 1, EN_ATENCION: 1 },
        ultimosReportes: [
            { id: 1, folio: 'REP-0001', nombreCiudadano: 'Camila Rojas', tipo: 'INCENDIO_FORESTAL', descripcion: '', latitud: -34.7, longitud: -71.0, sectorReferencia: 'Curva El Venado', gravedad: 'CRITICA', estado: 'EN_ATENCION', fechaCreacion: '' },
            { id: 2, folio: 'REP-0002', nombreCiudadano: 'Jorge Munoz', tipo: 'COLUMNA_DE_HUMO', descripcion: '', latitud: -34.7, longitud: -71.0, sectorReferencia: 'Cruce Los Pinos', gravedad: 'ALTA', estado: 'VALIDADO', fechaCreacion: '' }
        ],
        alertasVigentes: []
    };

    private readonly platformId = inject(PLATFORM_ID);

    constructor(private reportesService: ReportesService) {}

    ngOnInit(): void {
        // Solo consultar el BFF en el navegador (evita bloquear el prerender/SSR).
        if (!isPlatformBrowser(this.platformId)) {
            return;
        }
        this.reportesService.obtenerDashboard().pipe(timeout(4000)).subscribe({
            next: (d) => { this.data = d; this.cargando = false; },
            error: () => { this.data = this.demo; this.offline = true; this.cargando = false; }
        });
    }

    get ultimos(): Reporte[] { return this.data?.ultimosReportes ?? []; }
    get alertas(): Alerta[] { return this.data?.alertasVigentes ?? []; }

    badgeClass(g: string): string {
        if (g === 'CRITICA') return 'b-crit';
        if (g === 'ALTA') return 'b-alta';
        return 'b-norm';
    }
}
