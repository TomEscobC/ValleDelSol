import { Component, OnInit, OnDestroy, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';

declare var L: any; // Instancia global de Leaflet

@Component({
    selector: 'app-centro-comando',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './centro-comando.html',
    encapsulation: ViewEncapsulation.None,
    styles: [`
    .fade-in { animation: fadeIn 0.4s ease-in; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

    .map-wrapper-tactical { position: relative; width: 100%; height: 75vh; border-radius: 16px; overflow: hidden; border: 1px solid rgba(6, 182, 212, 0.3); box-shadow: 0 0 20px rgba(6, 182, 212, 0.2); }
    .map-layer { position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 0; background: #030805; }
    
    .floating-panel-right { position: absolute; top: 20px; right: 20px; z-index: 50; width: 380px; height: calc(100% - 40px); background: rgba(15, 23, 42, 0.7); backdrop-filter: blur(16px); border: 1px solid rgba(6, 182, 212, 0.3); border-radius: 16px; padding: 20px; display: flex; flex-direction: column; box-shadow: -10px 0 30px rgba(0,0,0,0.5); }
    
    .scroll-dark::-webkit-scrollbar { width: 6px; }
    .scroll-dark::-webkit-scrollbar-thumb { background: rgba(6, 182, 212, 0.4); border-radius: 10px; }

    /* ESTILOS MAPA COMANDO (Marcadores con pulso cian neón) */
    .custom-div-icon { background: transparent; border: none; }
    .map-marker-pro { font-size: 24px; position: relative; z-index: 10; filter: drop-shadow(0px 4px 6px rgba(0,0,0,0.8)); display: flex; align-items: center; justify-content: center; width: 40px; height: 40px; }
    .marker-pulse-bg { position: absolute; top: 0; left: 0; width: 100%; height: 100%; border-radius: 50%; z-index: -1; animation: mapPulse 2s infinite ease-out; }
    .bg-fire { background: rgba(239, 68, 68, 0.5); border: 1px solid #ef4444; }
    .bg-eq { background: rgba(168, 85, 247, 0.5); border: 1px solid #a855f7; }
    .bg-water { background: rgba(59, 130, 246, 0.5); border: 1px solid #3b82f6; }
    .bg-police { background: rgba(234, 179, 8, 0.5); border: 1px solid #eab308; }
    
    @keyframes mapPulse { 0% { transform: scale(0.8); opacity: 1; } 100% { transform: scale(2.5); opacity: 0; } }
  `]
})
export class CentroComandoComponent implements OnInit, OnDestroy {
    private mapInstance: any;

    mockHistorial = [
        { id: 'REP-088', dir: 'Curva El Venado, Ruta 5', lat: -34.75, lng: -71.05, tipo: 'Incendio Forestal', emoji: '🔥', bgClass: 'bg-fire' },
        { id: 'REP-089', dir: 'Puente San Jorge', lat: -34.60, lng: -70.90, tipo: 'Desborde Río', emoji: '🌊', bgClass: 'bg-water' },
        { id: 'REP-090', dir: 'Centro Chimbarongo', lat: -34.65, lng: -70.95, tipo: 'Sismo Fuerte', emoji: '🌋', bgClass: 'bg-eq' },
        { id: 'REP-091', dir: 'Cruce Los Pinos', lat: -34.70, lng: -71.00, tipo: 'Asistencia Policial', emoji: '🚓', bgClass: 'bg-police' }
    ];

    ngOnInit() {
        // Retardo estratégico para asegurar la existencia de #infoMap en el DOM renderizado
        setTimeout(() => this.initComandoMap(), 150);
    }

    initComandoMap() {
        if (this.mapInstance) return;

        // Centrado analítico en la zona de operaciones del Maule / O'Higgins
        this.mapInstance = L.map('infoMap', { zoomControl: false }).setView([-34.67, -70.98], 11);

        // Inyección de capa oscura (Dark Tiles de CartoDB)
        L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png').addTo(this.mapInstance);

        // Mapeo dinámico del historial de catástrofes con marcadores pulsantes
        this.mockHistorial.forEach(m => {
            const icon = L.divIcon({
                className: 'custom-div-icon',
                html: `<div class="map-marker-pro"><div class="marker-pulse-bg ${m.bgClass}"></div>${m.emoji}</div>`,
                iconSize: [40, 40]
            });

            L.marker([m.lat, m.lng], { icon })
                .addTo(this.mapInstance)
                .bindPopup(`<b style="color:#22d3ee; font-weight:800;">${m.tipo}</b><br><span style="color:#cbd5e1; font-size:0.8rem;">${m.dir}</span>`);
        });
    }

    ngOnDestroy() {
        // Limpieza de memoria RAM al desmontar la ruta
        if (this.mapInstance) {
            this.mapInstance.remove();
        }
    }
}