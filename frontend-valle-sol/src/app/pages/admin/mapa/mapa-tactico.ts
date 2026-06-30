import { Component, OnInit, OnDestroy, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';

declare var L: any;

@Component({
    selector: 'app-mapa-tactico',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './mapa-tactico.html',
    encapsulation: ViewEncapsulation.None,
    styles: [`
    .fade-in { animation: fadeIn 0.4s ease-in; }
    .map-wrapper-tactical { position: relative; width: 100%; height: 75vh; border-radius: 16px; overflow: hidden; border: 1px solid rgba(16, 185, 129, 0.3); box-shadow: 0 0 15px rgba(16, 185, 129, 0.4); }
    .map-layer { position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 0; background: #030805; }
    
    .floating-panel-left { position: absolute; top: 20px; left: 20px; z-index: 50; width: 340px; height: calc(100% - 100px); background: rgba(15, 23, 42, 0.65); backdrop-filter: blur(16px); border: 1px solid rgba(16, 185, 129, 0.2); border-radius: 16px; padding: 20px; display: flex; flex-direction: column; }
    .floating-toolbar { position: absolute; bottom: 20px; left: 20px; z-index: 50; background: rgba(15, 23, 42, 0.8); backdrop-filter: blur(12px); border: 1px solid rgba(255,255,255,0.1); border-radius: 30px; padding: 10px 20px; display: flex; gap: 20px; }
    .tool-btn { background: transparent; border: none; font-size: 1.2rem; cursor: pointer; filter: grayscale(100%); transition: 0.3s; opacity: 0.6; }
    .tool-btn:hover, .tool-btn.active { filter: grayscale(0%); opacity: 1; transform: scale(1.1); }
    .scroll-dark::-webkit-scrollbar { width: 6px; }
    .scroll-dark::-webkit-scrollbar-thumb { background: rgba(16, 185, 129, 0.4); border-radius: 10px; }

    /* Emojis Map Custom Leaflet */
    .custom-div-icon { background: transparent; border: none; }
    .map-marker-pro { font-size: 24px; position: relative; z-index: 10; filter: drop-shadow(0px 4px 6px rgba(0,0,0,0.8)); display: flex; align-items: center; justify-content: center; width: 40px; height: 40px; }
    .marker-pulse-bg { position: absolute; top: 0; left: 0; width: 100%; height: 100%; border-radius: 50%; z-index: -1; animation: mapPulse 2s infinite ease-out; }
    .bg-fire { background: rgba(239, 68, 68, 0.5); border: 1px solid #ef4444; }
    .bg-eq { background: rgba(168, 85, 247, 0.5); border: 1px solid #a855f7; }
    .bg-water { background: rgba(59, 130, 246, 0.5); border: 1px solid #3b82f6; }
    .bg-police { background: rgba(234, 179, 8, 0.5); border: 1px solid #eab308; }
    @keyframes mapPulse { 0% { transform: scale(0.8); opacity: 1; } 100% { transform: scale(2.5); opacity: 0; } }

    .btn-waze { background: rgba(14, 165, 233, 0.15); color: #0ea5e9; border: 1px solid rgba(14, 165, 233, 0.3); padding: 6px 12px; border-radius: 6px; font-size: 0.75rem; font-weight: bold; cursor: pointer; }
    .btn-maps { background: rgba(16, 185, 129, 0.15); color: #10b981; border: 1px solid rgba(16, 185, 129, 0.3); padding: 6px 12px; border-radius: 6px; font-size: 0.75rem; font-weight: bold; cursor: pointer; }
  `]
})
export class MapaTacticoComponent implements OnInit, OnDestroy {
    private mapInstance: any;

    mockHistorial = [
        { id: 'REP-088', dir: 'Curva El Venado, Ruta 5', lat: -34.75, lng: -71.05, emoji: '🔥', bgClass: 'bg-fire' },
        { id: 'REP-089', dir: 'Puente San Jorge', lat: -34.60, lng: -70.90, emoji: '🌊', bgClass: 'bg-water' },
        { id: 'REP-090', dir: 'Centro Chimbarongo', lat: -34.65, lng: -70.95, emoji: '🌋', bgClass: 'bg-eq' },
        { id: 'REP-091', dir: 'Cruce Los Pinos', lat: -34.70, lng: -71.00, emoji: '🚓', bgClass: 'bg-police' }
    ];

    ngOnInit() {
        // Pequeño timeout para asegurar que Angular ya pintó el <div> #radarMap
        setTimeout(() => this.initMap(), 100);
    }

    initMap() {
        if (this.mapInstance) return;

        this.mapInstance = L.map('radarMap', { zoomControl: false }).setView([-34.67, -70.98], 11);
        L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png').addTo(this.mapInstance);

        const latlngs: any[] = [];

        this.mockHistorial.forEach(m => {
            latlngs.push([m.lat, m.lng]);
            const icon = L.divIcon({
                className: 'custom-div-icon',
                html: `<div class="map-marker-pro"><div class="marker-pulse-bg ${m.bgClass}"></div>${m.emoji}</div>`,
                iconSize: [40, 40]
            });
            L.marker([m.lat, m.lng], { icon }).addTo(this.mapInstance);
        });

        L.polyline(latlngs, { color: '#10b981', weight: 3, opacity: 0.8, dashArray: '8, 8' }).addTo(this.mapInstance);
    }

    goToWaze(lat: number, lng: number) { window.open(`https://waze.com/ul?ll=${lat},${lng}&navigate=yes`, '_blank'); }
    goToMaps(lat: number, lng: number) { window.open(`http://maps.google.com/maps?q=$${lat},${lng}`, '_blank'); }

    ngOnDestroy() {
        if (this.mapInstance) {
            this.mapInstance.remove();
        }
    }
}