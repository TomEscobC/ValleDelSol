import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportesService } from '../../../core/services/reportes.service';
import { CrearReportePayload, ReporteRegistrado, Gravedad } from '../../../core/models/reporte.interface';

@Component({
  selector: 'app-reportar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  styles: [`
    .reportar-wrap { max-width: 720px; margin: 0 auto; }
    .glass-card-pro { background: rgba(15, 23, 42, 0.45); backdrop-filter: blur(12px); border: 1px solid rgba(16,185,129,0.18); border-radius: 18px; box-shadow: 0 10px 30px rgba(0,0,0,0.5); }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .full { grid-column: 1 / -1; }
    label { display:block; font-size:0.72rem; text-transform:uppercase; color:#64748b; font-weight:700; margin-bottom:6px; letter-spacing:1px; }
    .field { width:100%; background: rgba(2,6,23,0.7); border:1px solid rgba(16,185,129,0.2); padding:12px 14px; border-radius:10px; color:#f8fafc; font-size:0.92rem; outline:none; box-sizing:border-box; }
    .field:focus { border-color:#34d399; box-shadow:0 0 12px rgba(52,211,153,0.15); }
    textarea.field { resize: vertical; min-height: 84px; }
    .btn { background:#10b981; color:#022c22; font-weight:800; padding:13px 18px; border-radius:12px; border:none; cursor:pointer; transition:.25s; }
    .btn:hover { background:#34d399; box-shadow:0 0 18px rgba(52,211,153,0.4); }
    .btn:disabled { opacity:.5; cursor:not-allowed; }
    .btn-ghost { background:rgba(16,185,129,0.06); border:1px solid rgba(16,185,129,0.25); color:#34d399; padding:10px 14px; border-radius:10px; cursor:pointer; font-size:0.82rem; }
    .ok-card { border:1px solid rgba(16,185,129,0.4); background:rgba(16,185,129,0.07); border-radius:14px; padding:18px; }
    .evac-card { border:1px solid rgba(239,68,68,0.5); background:rgba(239,68,68,0.08); border-radius:14px; padding:16px; margin-top:14px; }
    .err-card { border:1px solid rgba(239,68,68,0.5); background:rgba(239,68,68,0.08); border-radius:14px; padding:16px; color:#fca5a5; }
    .badge { display:inline-block; padding:3px 10px; border-radius:20px; font-size:0.72rem; font-weight:700; }
  `],
  template: `
  <main class="fade-in reportar-wrap">
    <div style="margin-bottom: 20px;">
      <h1 style="font-size:1.9rem; font-weight:800; margin:0;">Reportar Emergencia <span style="color:#34d399;">🔥</span></h1>
      <p style="color:#94a3b8; font-size:0.9rem;">Tu reporte temprano puede reducir hasta un 40% la superficie afectada. Comunidad Valle del Sol.</p>
    </div>

    <!-- Formulario -->
    <form *ngIf="!resultado" class="glass-card-pro" style="padding:24px;" (ngSubmit)="enviar()" #f="ngForm">
      <div class="form-grid">
        <div class="full">
          <label>Nombre del vecino</label>
          <input class="field" name="nombre" [(ngModel)]="modelo.nombreCiudadano" required placeholder="Ej: Tomás Escobar">
        </div>

        <div>
          <label>Tipo de emergencia</label>
          <select class="field" name="tipo" [(ngModel)]="modelo.tipo" required>
            <option value="INCENDIO_FORESTAL">Incendio forestal</option>
            <option value="COLUMNA_DE_HUMO">Columna de humo</option>
            <option value="QUEMA_NO_AUTORIZADA">Quema no autorizada</option>
            <option value="FOCO_REACTIVADO">Foco reactivado</option>
            <option value="OTRO">Otro</option>
          </select>
        </div>

        <div>
          <label>Gravedad estimada</label>
          <select class="field" name="gravedad" [(ngModel)]="modelo.gravedad" required>
            <option value="BAJA">Baja</option>
            <option value="MEDIA">Media</option>
            <option value="ALTA">Alta (genera alerta)</option>
            <option value="CRITICA">Crítica (evacuación)</option>
          </select>
        </div>

        <div class="full">
          <label>Sector / referencia</label>
          <input class="field" name="sector" [(ngModel)]="modelo.sectorReferencia" placeholder="Ej: Curva El Venado, Ruta 5">
        </div>

        <div>
          <label>Latitud</label>
          <input class="field" type="number" step="any" name="lat" [(ngModel)]="modelo.latitud" required placeholder="-34.70">
        </div>
        <div>
          <label>Longitud</label>
          <input class="field" type="number" step="any" name="lon" [(ngModel)]="modelo.longitud" required placeholder="-71.01">
        </div>

        <div class="full">
          <button type="button" class="btn-ghost" (click)="usarMiUbicacion()">📍 Usar mi ubicación</button>
          <span *ngIf="geoMsg" style="color:#94a3b8; font-size:0.78rem; margin-left:10px;">{{ geoMsg }}</span>
        </div>

        <div class="full">
          <label>Descripción (mínimo 10 caracteres)</label>
          <textarea class="field" name="desc" [(ngModel)]="modelo.descripcion" required minlength="10"
            placeholder="Describe lo que observas: tamaño del foco, dirección del viento, cercanía a viviendas..."></textarea>
        </div>
      </div>

      <div *ngIf="error" class="err-card full" style="margin-top:14px;">⚠️ {{ error }}</div>

      <div style="margin-top:18px; display:flex; gap:12px; align-items:center;">
        <button class="btn" type="submit" [disabled]="enviando || f.invalid">
          {{ enviando ? 'Enviando...' : '🚨 Enviar reporte' }}
        </button>
        <span style="color:#64748b; font-size:0.78rem;">El reporte se persiste y se evalúa automáticamente.</span>
      </div>
    </form>

    <!-- Resultado -->
    <div *ngIf="resultado">
      <div class="ok-card">
        <h2 style="margin:0 0 6px 0; color:#34d399;">✅ Reporte recibido</h2>
        <p style="margin:0; color:#cbd5e1;">Folio <b style="font-family:monospace; color:#34d399;">{{ resultado.reporte.folio }}</b>
          — estado <span class="badge" style="background:rgba(16,185,129,0.12); color:#34d399;">{{ resultado.reporte.estado }}</span></p>
        <p style="margin:8px 0 0 0; color:#94a3b8; font-size:0.85rem;">{{ resultado.reporte.descripcion }}</p>

        <div *ngIf="resultado.alertaGenerada && resultado.alerta" class="evac-card">
          <span class="badge" style="background:rgba(239,68,68,0.15); color:#fca5a5;">{{ resultado.alerta.nivel }}</span>
          <h3 style="margin:8px 0 4px 0; color:#fca5a5;">{{ resultado.alerta.titulo }}</h3>
          <p style="margin:0; color:#fecaca; font-size:0.85rem;">{{ resultado.alerta.mensaje }}</p>
        </div>

        <div *ngIf="!resultado.alertaGenerada" style="margin-top:12px; color:#94a3b8; font-size:0.85rem;">
          ℹ️ Reporte registrado. Por su gravedad no se emitió alerta de evacuación a la comunidad.
        </div>

        <button class="btn" style="margin-top:18px;" (click)="nuevo()">Reportar otra emergencia</button>
      </div>
    </div>
  </main>
  `
})
export class ReportarComponent {

  modelo: CrearReportePayload = {
    nombreCiudadano: '',
    tipo: 'INCENDIO_FORESTAL',
    descripcion: '',
    latitud: null as unknown as number,
    longitud: null as unknown as number,
    sectorReferencia: '',
    gravedad: 'ALTA' as Gravedad
  };

  enviando = false;
  error = '';
  geoMsg = '';
  resultado: ReporteRegistrado | null = null;

  constructor(private reportesService: ReportesService) {}

  enviar() {
    this.error = '';
    this.enviando = true;
    this.reportesService.crearReporte(this.modelo).subscribe({
      next: (res) => {
        this.resultado = res;
        this.enviando = false;
      },
      error: (err) => {
        this.enviando = false;
        this.error = err?.error?.mensaje
          || 'No se pudo enviar el reporte. Verifica que el sistema (BFF) esté disponible.';
      }
    });
  }

  usarMiUbicacion() {
    if (typeof navigator === 'undefined' || !navigator.geolocation) {
      this.geoMsg = 'Geolocalización no disponible en este dispositivo.';
      return;
    }
    this.geoMsg = 'Obteniendo ubicación...';
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        this.modelo.latitud = Number(pos.coords.latitude.toFixed(5));
        this.modelo.longitud = Number(pos.coords.longitude.toFixed(5));
        this.geoMsg = 'Ubicación cargada.';
      },
      () => { this.geoMsg = 'No se pudo obtener la ubicación.'; }
    );
  }

  nuevo() {
    this.resultado = null;
    this.error = '';
    this.geoMsg = '';
    this.modelo = {
      nombreCiudadano: '', tipo: 'INCENDIO_FORESTAL', descripcion: '',
      latitud: null as unknown as number, longitud: null as unknown as number,
      sectorReferencia: '', gravedad: 'ALTA' as Gravedad
    };
  }
}
