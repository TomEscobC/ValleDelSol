import { Component, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  encapsulation: ViewEncapsulation.None,
  template: `
    <style>
      @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;700;800&display=swap');
      html, body { margin: 0; padding: 0; width: 100%; height: 100%; background-color: #030a06; overflow-x: hidden; font-family: 'Plus Jakarta Sans', sans-serif; color: #e2e8f0; }
      
      .forest-theme { background-color: #030a06; background-image: radial-gradient(circle at 50% 0%, #0a2517 0%, #030a06 75%); }
      .mockup-container { position: relative; min-height: 100vh; width: 100%; display: flex; flex-direction: column; align-items: center; }
      .grid-background { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background-image: linear-gradient(rgba(16, 185, 129, 0.02) 1px, transparent 1px), linear-gradient(90deg, rgba(16, 185, 129, 0.02) 1px, transparent 1px); background-size: 40px 40px; z-index: 0; pointer-events: none; }
      .ambient-glow { position: absolute; top: 15%; left: 50%; transform: translateX(-50%); width: 700px; height: 450px; background: radial-gradient(circle, rgba(16, 185, 129, 0.15) 0%, rgba(5, 150, 105, 0.03) 60%, transparent 80%); filter: blur(100px); z-index: 0; pointer-events: none; }
      
      .navbar { width: 100%; max-width: 1200px; display: flex; justify-content: space-between; align-items: center; padding: 25px 40px; z-index: 10; border-bottom: 1px solid rgba(16, 185, 129, 0.08); }
      .logo { font-size: 1.3rem; font-weight: 800; display: flex; align-items: center; gap: 8px; cursor: pointer; }
      .logo-icon { filter: drop-shadow(0 0 8px #10b981); }
      .fw-normal { font-weight: 400; color: #64748b; }
      .nav-links { list-style: none; display: flex; gap: 35px; font-size: 0.95rem; margin: 0; padding: 0; }
      .nav-links li { cursor: pointer; padding: 6px 0; position: relative; color: #94a3b8; transition: 0.2s; }
      .nav-links li:hover, .nav-links li.active { color: #34d399; }
      .nav-links li.active::after { content: ''; position: absolute; bottom: 0; left: 0; width: 100%; height: 2px; background: #10b981; box-shadow: 0 0 8px #10b981; }
      .nav-links a { text-decoration: none; color: inherit; }
      .nav-btn { background: rgba(16, 185, 129, 0.05); border: 1px solid rgba(16, 185, 129, 0.2); color: #34d399; padding: 8px 18px; border-radius: 8px; font-size: 0.9rem; cursor: pointer; transition: 0.2s; }
      .nav-btn:hover { background: rgba(16, 185, 129, 0.15); box-shadow: 0 0 12px rgba(16, 185, 129, 0.2); }
      .content-wrapper { width: 100%; max-width: 1200px; padding: 40px; z-index: 5; flex: 1; display: flex; flex-direction: column; }
      
      /* Utilidades Globales que usarán los otros componentes */
      .glass-card-pro { background: rgba(15, 23, 42, 0.4); backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px); border: 1px solid rgba(16, 185, 129, 0.15); border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,0.5); }
      .neon-shadow { box-shadow: 0 0 15px rgba(16, 185, 129, 0.4); }
      .text-emerald-neon { color: #34d399; text-shadow: 0 0 8px rgba(52, 211, 153, 0.5); }
      .scroll-dark::-webkit-scrollbar { width: 6px; }
      .scroll-dark::-webkit-scrollbar-thumb { background: rgba(16, 185, 129, 0.4); border-radius: 10px; }
      .fade-in { animation: fadeIn 0.4s ease-in; }
      @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
    </style>
    
    <div class="mockup-container forest-theme">
      <div class="grid-background"></div>
      <div class="ambient-glow"></div>

      <nav class="navbar">
        <div class="logo" routerLink="/admin/dashboard">
          <span class="logo-icon">🌲</span> ValleDelSol <span class="fw-normal">GIS</span>
        </div>
        
        <ul class="nav-links" *ngIf="role === 'admin'">
          <li routerLinkActive="active"><a routerLink="/admin/dashboard">Inicio</a></li>
          <li routerLinkActive="active"><a routerLink="/admin/mapa">Mapa GIS </a></li>
          <li routerLinkActive="active"><a routerLink="/admin/comando">Centro de Comando</a></li>
        </ul>

        <ul class="nav-links" *ngIf="role === 'ciudadano'">
          <li routerLinkActive="active"><a routerLink="/ciudadano/reportar">Reportar Emergencia</a></li>
          <li routerLinkActive="active"><a routerLink="/ciudadano/guardianes">Programa Guardián</a></li>
        </ul>

        <div style="display: flex; gap: 15px;">
          <button class="nav-btn" style="border-color: rgba(255,255,255,0.2); color: white;" (click)="toggleRole()">
            Modo: {{ role === 'admin' ? '🛡️ Admin' : '📱 Vecino' }}
          </button>
          <button class="nav-btn" routerLink="/login" *ngIf="role === 'admin'">🔓 Login AWS</button>
        </div>
      </nav>

      <div class="content-wrapper">
        <router-outlet></router-outlet>
      </div>
    </div>
  `
})
export class App {
  role: 'admin' | 'ciudadano' = 'admin';

  toggleRole() {
    this.role = this.role === 'admin' ? 'ciudadano' : 'admin';
  }
}