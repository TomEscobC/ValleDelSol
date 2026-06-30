import { Routes } from '@angular/router';

import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: '**',
    renderMode: RenderMode.Client,
  }
];

export const routes: Routes = [
  // Rutas Admin
  { path: 'admin/dashboard', loadComponent: () => import('./pages/admin/dashboard/dashboard.component').then(m => m.DashboardComponent) },
  { path: 'admin/mapa', loadComponent: () => import('./pages/admin/mapa/mapa-tactico').then(m => m.MapaTacticoComponent) },
  { path: 'admin/comando', loadComponent: () => import('./pages/admin/centro-comando/centro-comando').then(m => m.CentroComandoComponent) },

  // Rutas Ciudadano y Auth
  { path: 'ciudadano/reportar', loadComponent: () => import('./pages/ciudadano/reportar/reportar.component').then(m => m.ReportarComponent) },
  { path: 'ciudadano/guardianes', loadComponent: () => import('./pages/ciudadano/guardianes/guardianes.component').then(m => m.GuardianesComponent) },
  { path: 'login', loadComponent: () => import('./pages/auth/login/login.component').then(m => m.LoginComponent) },

  // Por defecto nos manda al dashboard
  { path: '', redirectTo: 'admin/dashboard', pathMatch: 'full' }


];